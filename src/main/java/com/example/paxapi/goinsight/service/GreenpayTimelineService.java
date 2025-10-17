package com.example.paxapi.goinsight.service;

import com.example.paxapi.goinsight.domain.GreenpaySnapshot;
import com.example.paxapi.goinsight.repository.GreenpaySnapshotRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GreenpayTimelineService {
  private static final ZoneId CI_ZONE = ZoneId.of("Africa/Abidjan");
  private final GreenpaySnapshotRepo repo;

  /** Derniers N buckets 30 min (00/30) en heure d'Abidjan */
  public List<LocalDateTime> lastNBuckets(int n) {
    if (n <= 0) n = 3;
    LocalDateTime now = LocalDateTime.now(CI_ZONE);
    LocalDateTime floor = floorTo30(now);
    List<LocalDateTime> r = new ArrayList<>(n);
    for (int i = n - 1; i >= 0; i--) r.add(floor.minusMinutes(30L * i));
    return r;
  }

  public List<GreenpaySnapshot> historyForSn(String sn, int n) {
    return repo.findByTerminalSnAndCapturedAtInOrderByCapturedAtAsc(sn, lastNBuckets(n));
  }

  public Map<String, List<GreenpaySnapshot>> historyForAll(int n) {
    var rows = repo.findByCapturedAtInOrderByCapturedAtAsc(lastNBuckets(n));
    return rows.stream().collect(Collectors.groupingBy(GreenpaySnapshot::getTerminalSn, LinkedHashMap::new, Collectors.toList()));
  }

  private static LocalDateTime floorTo30(LocalDateTime dt) {
    int floored = (dt.getMinute() / 30) * 30;
    return dt.withMinute(floored).withSecond(0).withNano(0);
  }
}
