package com.example.paxapi.kpi.web;

import com.example.paxapi.kpi.domain.KpiPolicy;
import com.example.paxapi.kpi.read.KpiReadDao;
import com.example.paxapi.kpi.service.KpiPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/kpi")
@RequiredArgsConstructor
public class KpiController {

  private final KpiReadDao readDao;
  private final KpiPolicyService svc;

  @PostMapping("/policies")
  public KpiPolicy upsertPolicy(@RequestBody KpiPolicy body) {
    return svc.createOrUpdate(body);
  }

  @GetMapping("/policies")
  public java.util.List<KpiPolicy> listPolicies() {
    return svc.listAll();
  }

  @PostMapping("/policies/lock")
  public void lockWeek(@RequestParam String weekStart, @RequestParam Long policyId) {
    svc.lockWeek(LocalDate.parse(weekStart), policyId);
  }

  @GetMapping("/daily")
  public org.springframework.data.domain.Page<?> daily(
      @RequestParam(required = false) String date,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "50") int size) {

    LocalDate d = (date == null || date.isBlank()) ? null : LocalDate.parse(date);
    return svc.daily(d, org.springframework.data.domain.PageRequest.of(page, size));
  }

  @GetMapping("/weekly")
  public org.springframework.data.domain.Page<?> weekly(
      @RequestParam(required = false) String weekStart,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "50") int size) {

    LocalDate w = (weekStart == null || weekStart.isBlank()) ? null : LocalDate.parse(weekStart);
    return svc.weekly(w, org.springframework.data.domain.PageRequest.of(page, size));
  }
}
