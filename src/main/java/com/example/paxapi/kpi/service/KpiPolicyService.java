package com.example.paxapi.kpi.service;

import com.example.paxapi.kpi.domain.KpiPolicy;
import com.example.paxapi.kpi.domain.KpiPolicyWeekLock;
import com.example.paxapi.kpi.read.KpiReadDao;
import com.example.paxapi.kpi.repo.KpiPolicyRepo;
import com.example.paxapi.kpi.repo.KpiPolicyWeekLockRepo;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiPolicyService {

  private final KpiPolicyRepo policyRepo;
  private final KpiPolicyWeekLockRepo weekLockRepo;
  private final KpiReadDao readDao;

  public KpiPolicy createOrUpdate(KpiPolicy body) {
    return policyRepo.save(body);
  }

public void lockWeek(LocalDate weekStart, Long policyId) {
  var policy = policyRepo.findById(policyId)
      .orElseThrow(() -> new IllegalArgumentException("Unknown policyId " + policyId));

  int weekYear   = weekStart.get(IsoFields.WEEK_BASED_YEAR);
  int weekNumber = weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

  var lock = KpiPolicyWeekLock.builder()
      .weekStart(weekStart)        // <<< IMPORTANT
      .weekYear(weekYear)
      .weekNumber(weekNumber)
      .policy(policy)
      .lockedAt(Instant.now())
      .build();

  weekLockRepo.save(lock);
}

  public Page<?> daily(LocalDate date, PageRequest page) {
    return readDao.daily(date, page);
  }

  public Page<?> weekly(LocalDate weekStart, PageRequest page) {
    return readDao.weekly(weekStart, page);
  }
  public java.util.List<KpiPolicy> listAll() {
    return policyRepo.findAll();
  }
}
