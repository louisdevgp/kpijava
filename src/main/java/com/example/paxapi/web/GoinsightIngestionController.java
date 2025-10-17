package com.example.paxapi.web;

import com.example.paxapi.goinsight.service.GoinsightGreenpayIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ingest/goinsight/greenpay")
@RequiredArgsConstructor
public class GoinsightIngestionController {

  private final GoinsightGreenpayIngestionService svc;

  /**
   * Lance l’ingestion fusionnée (base + batterie), en ignorant le paramètre insightId.
   * Conserve la même route qu’avant pour compatibilité.
   *
   * POST /api/ingest/goinsight/greenpay
   */
  @PostMapping
  public ResponseEntity<Map<String, Object>> run(
      @RequestParam(name = "insightId", required = false) String ignored // rétro-compat, non utilisé
  ) {
    int n = svc.ingestAll();
    return ResponseEntity.ok(Map.of(
        "status", "ok",
        "mode", "merged(base+battery)",
        "ingested", n
    ));
  }

  /**
   * Alias explicite si tu veux une route dédiée.
   * POST /api/ingest/goinsight/greenpay/all
   */
  @PostMapping("/all")
  public ResponseEntity<Map<String, Object>> runAll() {
    int n = svc.ingestAll();
    return ResponseEntity.ok(Map.of(
        "status", "ok",
        "mode", "merged(base+battery)",
        "ingested", n
    ));
  }
}
