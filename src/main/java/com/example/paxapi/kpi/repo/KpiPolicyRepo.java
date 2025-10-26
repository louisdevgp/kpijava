package com.example.paxapi.kpi.repo;

import com.example.paxapi.kpi.domain.KpiPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiPolicyRepo extends JpaRepository<KpiPolicy, Long> {
  KpiPolicy findTopByOrderByIdDesc();
}
