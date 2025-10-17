package com.example.paxapi.web;

import com.example.paxapi.goinsight.domain.GreenpaySnapshot;
import com.example.paxapi.goinsight.service.GreenpayTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tpe")
@RequiredArgsConstructor
public class GreenpayTimelineController {

  private final GreenpayTimelineService svc;

  // ex: GET /api/tpe/0821713172/history?count=3
  @GetMapping("/{sn}/history")
  public ResponseEntity<List<GreenpaySnapshot>> historyForSn(
      @PathVariable String sn,
      @RequestParam(defaultValue = "3") int count
  ) {
    return ResponseEntity.ok(svc.historyForSn(sn, count));
  }

  // ex: GET /api/tpe/history/last?count=3  → tous les TPE, groupés par SN
  @GetMapping("/history/last")
  public ResponseEntity<Map<String, List<GreenpaySnapshot>>> historyForAll(
      @RequestParam(defaultValue = "3") int count
  ) {
    return ResponseEntity.ok(svc.historyForAll(count));
  }
}
