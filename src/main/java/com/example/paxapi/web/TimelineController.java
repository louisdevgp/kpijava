package com.example.paxapi.web;

import com.example.paxapi.goinsight.domain.GreenpayTimelineView;
import com.example.paxapi.goinsight.repository.TimelineRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

  private final TimelineRepo repo;

  // Tous TPE sur une fenêtre
  // GET /api/timeline/window?from=2025-10-14T12:00:00&to=2025-10-14T13:00:00
  @GetMapping("/window")
  public Page<GreenpayTimelineView> byWindow(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      Pageable pageable
  ) {
    return repo.findById_CapturedAtBetweenOrderById_CapturedAtAsc(from, to, pageable);
  }

  // Un TPE sur une fenêtre
  // GET /api/timeline/{sn}/window?from=...&to=...
  @GetMapping("/{sn}/window")
  public Page<GreenpayTimelineView> bySnAndWindow(
      @PathVariable String sn,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      Pageable pageable
  ) {
    return repo.findById_TerminalSnAndId_CapturedAtBetweenOrderById_CapturedAtAsc(sn, from, to, pageable);
  }
}
