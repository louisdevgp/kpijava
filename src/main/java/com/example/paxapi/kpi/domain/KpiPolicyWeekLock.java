package com.example.paxapi.kpi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "kpi_policy_week_lock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KpiPolicyWeekLock {

  // Clé = lundi ISO de la semaine
  @Id
  @Column(name = "week_start", nullable = false)
  private LocalDate weekStart;

  @Column(name = "policy_id", nullable = false)
  private Long policyId;

  @Column(name = "week_number")
  private Integer weekNumber;

  @Column(name = "week_year")
  private Integer weekYear;

  // si la colonne est gérée en DB (DEFAULT CURRENT_TIMESTAMP), on ne touche pas
  @Column(name = "locked_at", insertable = false, updatable = false)
  private OffsetDateTime lockedAt;
}
