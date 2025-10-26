package com.example.paxapi.kpi.repo;

import com.example.paxapi.kpi.domain.KpiPolicyWeekLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface KpiPolicyWeekLockRepo extends JpaRepository<KpiPolicyWeekLock, LocalDate> {
  Optional<KpiPolicyWeekLock> findByWeekStart(LocalDate weekStart);
}
