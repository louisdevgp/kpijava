package com.example.paxapi.goinsight.repository;

import com.example.paxapi.goinsight.domain.GreenpaySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GreenpaySnapshotRepo extends JpaRepository<GreenpaySnapshot, Long> {

  // Dernier snapshot d’un terminal
  Optional<GreenpaySnapshot> findTopByTerminalSnOrderByCapturedAtDesc(String terminalSn);

  // Un snapshot précis (SN + bucket)
  Optional<GreenpaySnapshot> findByTerminalSnAndCapturedAt(String terminalSn, LocalDateTime capturedAt);

  // >>> Méthodes attendues par GreenpayTimelineService <<<
  // Timeline d’un terminal sur une liste de buckets
  List<GreenpaySnapshot> findByTerminalSnAndCapturedAtInOrderByCapturedAtAsc(
      String terminalSn,
      List<LocalDateTime> capturedAt
  );

  // Timeline globale (tous terminaux) sur une liste de buckets
  List<GreenpaySnapshot> findByCapturedAtInOrderByCapturedAtAsc(
      List<LocalDateTime> capturedAt
  );
}
