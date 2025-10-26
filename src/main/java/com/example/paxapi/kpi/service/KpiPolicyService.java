package com.example.paxapi.kpi.service;

import com.example.paxapi.kpi.domain.KpiPolicy;
import com.example.paxapi.kpi.repo.KpiPolicyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KpiPolicyService {
  private final KpiPolicyRepo repo;

  public Page<KpiPolicy> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public Optional<KpiPolicy> get(Long id) {
    return repo.findById(id);
  }

  public KpiPolicy create(KpiPolicy body) {
    // aucune logique: on stocke tel quel (les flags/thresholds sont utilisés au Front)
    return repo.save(body);
  }

  public KpiPolicy update(Long id, KpiPolicy body) {
    body.setId(id);
    return repo.save(body);
  }

  public void delete(Long id) {
    repo.deleteById(id);
  }

  public Optional<KpiPolicy> latest() {
    return Optional.ofNullable(repo.findTopByOrderByIdDesc());
  }
}
