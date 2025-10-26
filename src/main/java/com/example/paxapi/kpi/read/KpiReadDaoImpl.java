package com.example.paxapi.kpi.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class KpiReadDaoImpl implements KpiReadDao {

  @PersistenceContext
  private EntityManager em;

  @Override
  public Page<DailyRow> daily(LocalDate date, Pageable pageable) {
    LocalDate d = (date == null ? LocalDate.now() : date);

    String baseSelect = """
        SELECT date,
               terminal_sn,
               merchant,
               ok_count,
               fail_count,
               checks,
               decision,
               score,
               internet_ok,
               tpe_on_ok,
               geofence_ok,
               battery_ok,
               printer_ok,
               paper_ok
        FROM vw_tpe_daily_kpi
        WHERE date = :d
        ORDER BY terminal_sn
        """;

    String countSql = "SELECT COUNT(*) FROM vw_tpe_daily_kpi WHERE date = :d";

    Query q = em.createNativeQuery(baseSelect)
        .setParameter("d", Date.valueOf(d))
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize());

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    List<DailyRow> content = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      DailyRow dto = new DailyRow(
          ((Date) r[0]).toLocalDate(),
          (String) r[1],
          (String) r[2],
          toInt(r[3]), toInt(r[4]), toInt(r[5]),
          toInt(r[6]), toInt(r[7]),
          toBool(r[8]), toBool(r[9]), toBool(r[10]),
          toBool(r[11]), toBool(r[12]), toBool(r[13])
      );
      content.add(dto);
    }

    Number total = (Number) em.createNativeQuery(countSql)
        .setParameter("d", Date.valueOf(d))
        .getSingleResult();

    return new PageImpl<>(content, pageable, total.longValue());
  }

  @Override
  public Page<WeeklyRow> weekly(LocalDate weekStart, Pageable pageable) {
    // par défaut, semaine du jour courant (lundi)
    LocalDate ws = (weekStart == null)
        ? LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() % 7)
        : weekStart;

    String baseSelect = """
        SELECT week_start,
               terminal_sn,
               merchant,
               days_ok,
               days_fail,
               decision,
               score
        FROM vw_tpe_weekly_kpi
        WHERE week_start = :ws
        ORDER BY terminal_sn
        """;

    String countSql = "SELECT COUNT(*) FROM vw_tpe_weekly_kpi WHERE week_start = :ws";

    Query q = em.createNativeQuery(baseSelect)
        .setParameter("ws", Date.valueOf(ws))
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize());

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    List<WeeklyRow> content = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      WeeklyRow dto = new WeeklyRow(
          ((Date) r[0]).toLocalDate(),
          (String) r[1],
          (String) r[2],
          toInt(r[3]), toInt(r[4]), toInt(r[5]), toInt(r[6])
      );
      content.add(dto);
    }

    Number total = (Number) em.createNativeQuery(countSql)
        .setParameter("ws", Date.valueOf(ws))
        .getSingleResult();

    return new PageImpl<>(content, pageable, total.longValue());
  }

  private static Integer toInt(Object o) {
    if (o == null) return null;
    if (o instanceof Integer) return (Integer) o;
    if (o instanceof Long) return ((Long) o).intValue();
    if (o instanceof BigInteger) return ((BigInteger) o).intValue();
    if (o instanceof Short) return ((Short) o).intValue();
    if (o instanceof Byte) return ((Byte) o).intValue();
    return Integer.valueOf(o.toString());
  }

  private static Boolean toBool(Object o) {
    if (o == null) return null;
    if (o instanceof Boolean) return (Boolean) o;
    if (o instanceof Number) return ((Number) o).intValue() != 0;
    return "1".equals(o.toString()) || "true".equalsIgnoreCase(o.toString());
  }
}
