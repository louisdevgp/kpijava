package com.example.paxapi.goinsight.service;

import com.example.paxapi.goinsight.domain.GreenpayRecord;
import com.example.paxapi.goinsight.domain.GreenpaySnapshot;
import com.example.paxapi.goinsight.repository.GreenpayRecordRepo;
import com.example.paxapi.goinsight.repository.GreenpaySnapshotRepo;
import com.pax.market.api.sdk.java.api.base.dto.Result;
import com.pax.market.api.sdk.java.api.goinsight.GoInsightApi;
import com.pax.market.api.sdk.java.api.goinsight.dto.DataQueryResultDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoinsightGreenpayIngestionService {

  private static final ZoneId CI_ZONE = ZoneId.of("Africa/Abidjan");

  private final GoInsightApi goInsightApi;
  private final GreenpayRecordRepo recordRepo;
  private final GreenpaySnapshotRepo snapshotRepo;

  @PersistenceContext
  private EntityManager em;

  /** Insight “base” (localisation, opérateur, etc.) */
  @Value("${goinsight.greenpay.insight-id}")
  private String baseInsightId;

  /** Insight “Info de charge TPE” (batterie) */
  @Value("${goinsight.greenpay.battery-insight-id}")
  private String batteryInsightId;

  @Value("${goinsight.page-size:500}")
  private int pageSize;

  @Value("${goinsight.full-history:false}")
  private boolean fullHistory;

  /** Garde-fou pour éviter les boucles paginées infinies */
  @Value("${goinsight.max-pages:300}")
  private int maxPages;

  /* =================== API publique =================== */

  /**
   * Ingestion fusionnée : charge l’insight “base” + l’insight “batterie”,
   * fusionne par SN, puis upsert:
   *  - 1 ligne dans goinsight_greenpay_record (PK=terminal_sn)
   *  - 1 ligne dans goinsight_greenpay_snapshot (PK logique=terminal_sn+bucket 30min)
   */
  @Transactional
  public int ingestAll() {
    PagedInvoker invoker = resolvePagedInvoker();
    log.info("Resolved GoInsight paged overload: {}", invoker.kind);

    Map<String, Map<String, String>> base = collectRowsToMap(baseInsightId, invoker);
    Map<String, Map<String, String>> batt = collectRowsToMap(batteryInsightId, invoker);

    Set<String> sns = new HashSet<>(base.keySet());
    sns.addAll(batt.keySet());

    int total = 0;
    int cntCharge = 0, cntAvg = 0, cntCons = 0;
    LocalDateTime bucket = floorTo30(LocalDateTime.now(CI_ZONE));

    for (String sn : sns) {
      Map<String, String> merged = new HashMap<>();
      Map<String, String> mBase = base.get(sn);
      Map<String, String> mBatt = batt.get(sn);
      if (mBase != null) merged.putAll(mBase);
      if (mBatt != null) merged.putAll(mBatt); // la batterie complète/écrase au besoin

      GreenpayRecord entity = toEntity(merged);
      if (entity.getTerminalSn() == null) {
        log.warn("Skip merged row without terminalSn. Keys={}", merged.keySet());
        continue;
      }

      // stats batterie mappée
      if (entity.getIsCharging() != null) cntCharge++;
      if (entity.getBatteryRateAvg() != null) cntAvg++;
      if (entity.getBatteryRateConsume() != null) cntCons++;

      // Upsert record (PK = terminal_sn)
      recordRepo.save(entity);
      total++;

      // Upsert snapshot (PK logique: terminal_sn + bucket)
      snapshotRepo.findByTerminalSnAndCapturedAt(entity.getTerminalSn(), bucket)
          .map(s -> {
            copyToSnapshot(entity, s);
            return snapshotRepo.save(s);
          })
          .orElseGet(() -> {
            GreenpaySnapshot s = GreenpaySnapshot.builder()
                .terminalSn(entity.getTerminalSn())
                .capturedAt(bucket)
                .build();
            copyToSnapshot(entity, s);
            return snapshotRepo.save(s);
          });

      if ((total % 1000) == 0) {
        em.flush();
        em.clear();
      }
    }

    em.flush();
    em.clear();

    log.info("Merged ingestion done: {} terminals (base={}, battery={}, bucket={})",
        total, base.size(), batt.size(), bucket);
    log.info("Battery mapped counters: is_charging={} battery_rate_avg={} battery_rate_consume={}",
        cntCharge, cntAvg, cntCons);
    return total;
  }

  /* =============== Collecte (paged safe) =============== */

  private Map<String, Map<String, String>> collectRowsToMap(String insightId, PagedInvoker invoker) {
    if (insightId == null || insightId.isBlank()) {
      log.warn("collectRowsToMap called with empty insightId");
      return Map.of();
    }
    if (invoker.isPaged()) return collectPaged(insightId, invoker);
    return collectSingle(insightId);
  }

  private Map<String, Map<String, String>> collectPaged(String id, PagedInvoker invoker) {
    Map<String, Map<String, String>> out = new HashMap<>();
    int pageNo = 1;

    // détection de répétition
    String lastSig = null;
    int sameSigCount = 0;
    Integer totalPagesMetaSeen = null;

    while (true) {
      if (pageNo > maxPages) {
        log.warn("Stop {}: reached max-pages={}", id, maxPages);
        break;
      }

      Result<DataQueryResultDTO> res = invoker.call(goInsightApi, id, pageNo, pageSize, fullHistory);
      if (!ok(res, id, "page " + pageNo)) break;

      DataQueryResultDTO data = res.getData();

      // meta pagination (si exposées par le SDK)
      Integer pageNoMeta     = metaInt(data, "getPageNo");
      Integer pageSizeMeta   = metaInt(data, "getPageSize");
      Integer totalPagesMeta = metaInt(data, "getTotalPages");
      if (totalPagesMeta == null) totalPagesMeta = metaInt(data, "getTotalPage");
      Boolean hasNextMeta    = metaBool(data, "getHasNext");
      if (totalPagesMeta != null && totalPagesMeta > 0) totalPagesMetaSeen = totalPagesMeta;

      int pageCount = 0;
      String firstSn = null, lastSn = null;

      for (Object rowObj : data.getRows()) {
        List<?> cells = extractCells(rowObj);

        Map<String, String> map = new HashMap<>();
        for (Object cell : cells) {
          String colName = normalize(invokeStringGetter(cell, "getColName"));
          String value   = normalize(invokeStringGetter(cell, "getValue"));
          if (colName != null) map.put(colName, value);
        }

        // Le SN peut s'appeler sys_terminal ou terminal_sn selon l’insight
        String sn = normalize(map.getOrDefault("sys_terminal", map.get("terminal_sn")));
        if (sn != null) {
          if (firstSn == null) firstSn = sn;
          lastSn = sn;
          out.put(sn, map); // dernière occurrence gagnante
          pageCount++;
        }
      }

      String sig = (firstSn == null && lastSn == null)
          ? ("count:" + pageCount)
          : ("first:" + firstSn + "|last:" + lastSn + "|count:" + pageCount);

      if (Objects.equals(sig, lastSig)) sameSigCount++; else { sameSigCount = 0; lastSig = sig; }

      log.info("Insight {} page {} -> {} rows (sig={}, pageNoMeta={}, pageSizeMeta={}, totalPagesMeta={}, hasNext={})",
          id, pageNo, pageCount, sig, pageNoMeta, pageSizeMeta, totalPagesMeta, hasNextMeta);

      // conditions d'arrêt
      if (pageCount < pageSize) {
        log.info("Last page by size: {} < {}", pageCount, pageSize);
        break;
      }
      if (Boolean.FALSE.equals(hasNextMeta)) {
        log.info("Last page by hasNext=false");
        break;
      }
      if (totalPagesMetaSeen != null && pageNo >= totalPagesMetaSeen) {
        log.info("Reached totalPages={}, stop.", totalPagesMetaSeen);
        break;
      }
      if (sameSigCount >= 2) { // 3 pages de suite identiques
        log.warn("Repeating page signature detected 3 times, stop to avoid infinite loop.");
        break;
      }

      pageNo++;
    }
    return out;
  }

  private Map<String, Map<String, String>> collectSingle(String id) {
    Map<String, Map<String, String>> out = new HashMap<>();
    Result<DataQueryResultDTO> res = goInsightApi.findDataFromInsight(id);
    if (!ok(res, id, "single")) return out;

    for (Object rowObj : res.getData().getRows()) {
      List<?> cells = extractCells(rowObj);

      Map<String, String> map = new HashMap<>();
      for (Object cell : cells) {
        String colName = normalize(invokeStringGetter(cell, "getColName"));
        String value   = normalize(invokeStringGetter(cell, "getValue"));
        if (colName != null) map.put(colName, value);
      }
      String sn = normalize(map.getOrDefault("sys_terminal", map.get("terminal_sn")));
      if (sn != null) out.put(sn, map);
    }
    return out;
  }

  private boolean ok(Result<DataQueryResultDTO> res, String id, String where) {
    if (res == null) throw new IllegalStateException("GoInsight null (" + id + ", " + where + ")");
    Integer code = res.getBusinessCode();
    String msg   = Optional.ofNullable(res.getMessage()).orElse("no message");
    if (code != null && code != 0) {
      throw new RuntimeException("GoInsight error (" + code + ") [" + id + "][" + where + "]: " + msg);
    }
    var data = res.getData();
    if (data == null || data.getRows() == null || data.getRows().isEmpty()) {
      log.warn("GoInsight success but empty data [{}][{}]", id, where);
      return false;
    }
    return true;
  }

  /* ================= mapping ================= */

  private GreenpayRecord toEntity(Map<String, String> m) {
    String locationRaw = val(m, "location");
    Double[] latlon = parseLatLon(locationRaw);

    return GreenpayRecord.builder()
        // Identité
        .terminalSn(valAny(m, "sys_terminal", "terminal_sn"))
        .merchant(valAny(m, "sys_merchant", "merchant"))
        .model(valAny(m, "sys_model", "model"))
        .status(val(m, "status"))
        .printer(val(m, "printer"))

        // Géoloc
        .locationRaw(locationRaw)
        .latitude(latlon[0])
        .longitude(latlon[1])
        .geofence(valAny(m, "isOutOfRange", "geofence"))

        // Santé / réseau
        .batteryHealthy(valAny(m, "CALC_1000004237", "battery_healthy"))
        .offlineDuration(valAny(m, "offline_duration_by_day", "offline_duration"))
        .mobileCarrier(valAny(m, "mobliecarrier", "mobile_carrier"))
        .iccid(valAny(m, "ccid", "iccid"))
        .signal(valAny(m, "singal", "signal"))

        // Batterie (insight “Info de charge TPE”)
        .isCharging(parseBoolean(valAny(m, "is_charging","charging","isCharging")))
        .batteryRateAvg(parseDecimal(valAny(m, "battery_rate_avg","battery_remaining","battery_rate")))
        .batteryRateConsume(parseDecimal(valAny(m, "battery_rate_consume","battery_consume","battery_usage")))
        .build();
  }

  private static void copyToSnapshot(GreenpayRecord e, GreenpaySnapshot s) {
    s.setMerchant(e.getMerchant());
    s.setModel(e.getModel());
    s.setStatus(e.getStatus());
    s.setPrinter(e.getPrinter());
    s.setLocationRaw(e.getLocationRaw());
    s.setLatitude(e.getLatitude());
    s.setLongitude(e.getLongitude());
    s.setGeofence(e.getGeofence());
    s.setBatteryHealthy(e.getBatteryHealthy());
    s.setOfflineDuration(e.getOfflineDuration());
    s.setMobileCarrier(e.getMobileCarrier());
    s.setIccid(e.getIccid());
    s.setSignal(e.getSignal());
    // batterie
    s.setIsCharging(e.getIsCharging());
    s.setBatteryRateAvg(e.getBatteryRateAvg());
    s.setBatteryRateConsume(e.getBatteryRateConsume());
  }

  /* =========== reflection / pagination helpers =========== */

  private PagedInvoker resolvePagedInvoker() {
    Method[] methods = GoInsightApi.class.getMethods();
    List<Method> candidates = new ArrayList<>();
    for (Method m : methods) if ("findDataFromInsight".equals(m.getName())) candidates.add(m);

    for (Method m : candidates) {
      Class<?>[] p = m.getParameterTypes();
      if (p.length == 4 && p[0] == String.class && p[1].isEnum()
          && (p[2] == int.class || p[2] == Integer.class)
          && (p[3] == int.class || p[3] == Integer.class))
        return PagedInvoker.rangeEnum(m);
    }
    for (Method m : candidates) {
      Class<?>[] p = m.getParameterTypes();
      if (p.length == 3 && p[0] == String.class
          && (p[1] == int.class || p[1] == Integer.class)
          && (p[2] == int.class || p[2] == Integer.class))
        return PagedInvoker.simple(m);
    }
    for (Method m : candidates) {
      Class<?>[] p = m.getParameterTypes();
      if (p.length == 5 && p[0] == String.class
          && p[1] == java.util.Date.class && p[2] == java.util.Date.class
          && (p[3] == int.class || p[3] == Integer.class)
          && (p[4] == int.class || p[4] == Integer.class))
        return PagedInvoker.dateRange(m);
    }
    for (Method m : candidates) {
      Class<?>[] p = m.getParameterTypes();
      if (p.length == 5 && p[0] == String.class
          && (p[1] == long.class || p[1] == Long.class)
          && (p[2] == long.class || p[2] == Long.class)
          && (p[3] == int.class || p[3] == Integer.class)
          && (p[4] == int.class || p[4] == Integer.class))
        return PagedInvoker.epochRange(m);
    }
    return PagedInvoker.none();
  }

  static class PagedInvoker {
    enum Kind { NONE, RANGE_ENUM, SIMPLE, DATE_RANGE, EPOCH_RANGE }
    final Kind kind;
    final Method method;
    private PagedInvoker(Kind k, Method m) { this.kind = k; this.method = m; }
    static PagedInvoker none() { return new PagedInvoker(Kind.NONE, null); }
    static PagedInvoker rangeEnum(Method m) { return new PagedInvoker(Kind.RANGE_ENUM, m); }
    static PagedInvoker simple(Method m) { return new PagedInvoker(Kind.SIMPLE, m); }
    static PagedInvoker dateRange(Method m) { return new PagedInvoker(Kind.DATE_RANGE, m); }
    static PagedInvoker epochRange(Method m) { return new PagedInvoker(Kind.EPOCH_RANGE, m); }
    boolean isPaged() { return kind != Kind.NONE; }

    @SuppressWarnings("unchecked")
    Result<DataQueryResultDTO> call(GoInsightApi api, String id, int pageNo, int size, boolean fullHistory) {
      try {
        switch (kind) {
          case RANGE_ENUM -> {
            GoInsightApi.TimestampRangeType chosen = pickRangeEnum(fullHistory);
            return (Result<DataQueryResultDTO>) method.invoke(api, id, chosen, pageNo, size);
          }
          case SIMPLE -> {
            return (Result<DataQueryResultDTO>) method.invoke(api, id, pageNo, size);
          }
          case DATE_RANGE -> {
            Date from = fullHistory ? new Date(0L) : new Date(System.currentTimeMillis() - 24L*3600_000L);
            Date to   = new Date();
            return (Result<DataQueryResultDTO>) method.invoke(api, id, from, to, pageNo, size);
          }
          case EPOCH_RANGE -> {
            long from = fullHistory ? 0L : (System.currentTimeMillis() - 24L*3600_000L);
            long to   = System.currentTimeMillis();
            return (Result<DataQueryResultDTO>) method.invoke(api, id, from, to, pageNo, size);
          }
          default -> throw new IllegalStateException("Not paged");
        }
      } catch (Exception e) {
        throw new RuntimeException("Paged call failed (kind=" + kind + ", page=" + pageNo + ")", e);
      }
    }

    private GoInsightApi.TimestampRangeType pickRangeEnum(boolean fullHistory) {
      try {
        GoInsightApi.TimestampRangeType[] all = GoInsightApi.TimestampRangeType.values();
        Map<String, GoInsightApi.TimestampRangeType> byName = new HashMap<>();
        for (var t : all) byName.put(t.name(), t);
        if (fullHistory) {
          for (String w : List.of("ALL","HISTORY","TOTAL","THIS_YEAR","LAST_YEAR","LAST_90_DAYS","LAST_60_DAYS","LAST_30_DAYS"))
            if (byName.containsKey(w)) return byName.get(w);
        }
        if (byName.containsKey("NONE"))  return byName.get("NONE");
        if (byName.containsKey("TODAY")) return byName.get("TODAY");
        return all.length > 0 ? all[0] : null;
      } catch (Throwable t) {
        return null;
      }
    }
  }

  /* ================= utilitaires ================= */

  private static List<?> extractCells(Object rowObj) {
    try {
      var m = rowObj.getClass().getMethod("getCells");
      Object ret = m.invoke(rowObj);
      return (ret instanceof List) ? (List<?>) ret : List.of();
    } catch (NoSuchMethodException nsme) {
      return (rowObj instanceof List) ? (List<?>) rowObj : List.of();
    } catch (Exception e) {
      return List.of();
    }
  }

  private static String invokeStringGetter(Object target, String method) {
    try {
      var m = target.getClass().getMethod(method);
      Object v = m.invoke(target);
      return (v != null) ? String.valueOf(v) : null;
    } catch (Exception e) {
      return null;
    }
  }

  private static Integer metaInt(Object obj, String getter) {
    try {
      var m = obj.getClass().getMethod(getter);
      Object v = m.invoke(obj);
      return (v instanceof Number) ? ((Number) v).intValue() : null;
    } catch (Exception e) { return null; }
  }

  private static Boolean metaBool(Object obj, String getter) {
    try {
      var m = obj.getClass().getMethod(getter);
      Object v = m.invoke(obj);
      return (v instanceof Boolean) ? (Boolean) v : null;
    } catch (Exception e) { return null; }
  }

  private static String normalize(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private static String val(Map<String, String> m, String key) {
    return normalize(m.get(key));
  }

  /** essaie plusieurs clés possibles pour la même donnée (alias entre insights) */
  private static String valAny(Map<String, String> m, String... keys) {
    for (String k : keys) {
      String v = normalize(m.get(k));
      if (v != null) return v;
    }
    return null;
  }

  private static Double[] parseLatLon(String raw) {
    try {
      if (raw == null || raw.isBlank()) return new Double[]{null, null};
      String[] parts = raw.split(",");
      if (parts.length != 2) return new Double[]{null, null};
      return new Double[]{
          Double.valueOf(parts[0].trim()),
          Double.valueOf(parts[1].trim())
      };
    } catch (Exception e) {
      return new Double[]{null, null};
    }
  }

  private static LocalDateTime floorTo30(LocalDateTime dt) {
    int floored = (dt.getMinute() / 30) * 30; // 0 ou 30
    return dt.withMinute(floored).withSecond(0).withNano(0);
  }

  private static Boolean parseBoolean(String v) {
    if (v == null) return null;
    String t = v.trim().toLowerCase(Locale.ROOT);
    if (t.equals("true") || t.equals("1") || t.equals("yes") || t.equals("y")) return Boolean.TRUE;
    if (t.equals("false") || t.equals("0") || t.equals("no") || t.equals("n")) return Boolean.FALSE;
    return null;
  }

  /** parsing robuste: gère , . % espaces */
  private static BigDecimal parseDecimal(String v) {
    if (v == null) return null;
    String cleaned = v.trim();
    if (cleaned.isEmpty()) return null;
    cleaned = cleaned.replaceAll("[^0-9,.-]", ""); // enlève % et autres
    if (cleaned.contains(",") && cleaned.contains(".")) {
      // “1,234.56” → enlever séparateur de milliers
      cleaned = cleaned.replace(",", "");
    } else {
      // “0,39” → virgule comme décimal
      cleaned = cleaned.replace(',', '.');
    }
    try {
      return cleaned.isBlank() ? null : new BigDecimal(cleaned);
    } catch (Exception e) {
      return null;
    }
  }
}
