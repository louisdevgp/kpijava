// src/main/java/com/example/paxapi/kpi/domain/KpiPolicyWeekLock.java
package com.example.paxapi.kpi.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "kpi_policy_week_lock")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class KpiPolicyWeekLock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "week_start", nullable = false)
  private LocalDate weekStart;

  @Column(name = "week_year", nullable = false)
  private int weekYear;

  @Column(name = "week_number", nullable = false)
  private int weekNumber;

  @ManyToOne(optional = false)
  @JoinColumn(name = "policy_id", nullable = false)
  private KpiPolicy policy;

  @Column(name = "locked_at", nullable = false)
  private Instant lockedAt;
}
