package com.example.paxapi.web;

import com.example.paxapi.scheduler.GoinsightScheduler;
import com.example.paxapi.scheduler.IngestionCoordinator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/ingest")
@RequiredArgsConstructor
public class AdminIngestionController {

  private final IngestionCoordinator coordinator;
  private final GoinsightScheduler scheduler;

  /** Run manuel.
   *  full=true  -> force "full-history" pour CE run
   *  force=true -> ignore le verrou courant (dernier recours)
   */
  @PostMapping("/run")
  public ResponseEntity<Map<String, Object>> run(
      @RequestParam(defaultValue = "false") boolean full,
      @RequestParam(defaultValue = "false") boolean force
  ) {
    boolean started = coordinator.trigger("manual", full, force);
    return ResponseEntity.ok(Map.of(
        "started", started,
        "fullHistory", full,
        "force", force,
        "status", coordinator.status()
    ));
  }

  /** Statut courant (running, elapsed, etc.). */
  @GetMapping("/status")
  public ResponseEntity<IngestionCoordinator.Status> status() {
    return ResponseEntity.ok(coordinator.status());
  }

  /** Réinitialise le verrou (si tu es sûr qu’aucun run n’est actif). */
  @PostMapping("/reset-lock")
  public ResponseEntity<Map<String, String>> resetLock() {
    coordinator.resetLock();
    return ResponseEntity.ok(Map.of("result", "ok", "message", "lock reset"));
  }
}
