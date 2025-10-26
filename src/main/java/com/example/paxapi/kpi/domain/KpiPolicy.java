package com.example.paxapi.kpi.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kpi_policy")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KpiPolicy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 128, nullable = false)
  private String name;

  // Flags d’activation des critères (BOOLEAN -> TINYINT(1) côté MySQL)
  @Column(name = "use_internet")
  private Boolean useInternet;

  @Column(name = "use_tpe_on")
  private Boolean useTpeOn;

  @Column(name = "use_geofence")
  private Boolean useGeofence;

  @Column(name = "use_battery")
  private Boolean useBattery;

  @Column(name = "use_printer")
  private Boolean usePrinter;

  @Column(name = "use_paper")
  private Boolean usePaper;

  // Seuils / paramètres
  @Column(name = "daily_x")
  private Integer dailyX;

  @Column(name = "daily_fail_n")
  private Integer dailyFailN;

  @Column(name = "weekly_x")
  private Integer weeklyX;

  @Column(name = "weekly_fail_n")
  private Integer weeklyFailN;
}
