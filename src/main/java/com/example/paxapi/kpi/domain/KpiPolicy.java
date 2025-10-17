package com.example.paxapi.kpi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "kpi_policy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name = "name", nullable = false, length = 64)
    private String name = "DEFAULT";

    // Activation des critères
    @Builder.Default
    @Column(name = "use_internet", nullable = false)
    private boolean useInternet = true;

    @Builder.Default
    @Column(name = "use_tpe_on", nullable = false)
    private boolean useTpeOn = true;

    @Builder.Default
    @Column(name = "use_geofence", nullable = false)
    private boolean useGeofence = true;

    @Builder.Default
    @Column(name = "use_battery", nullable = false)
    private boolean useBattery = true;

    @Builder.Default
    @Column(name = "use_printer", nullable = false)
    private boolean usePrinter = true;

    @Builder.Default
    @Column(name = "use_paper", nullable = false)
    private boolean usePaper = true;

    // Seuils N / X
    @Builder.Default
    @Column(name = "daily_fail_N", nullable = false)
    private int dailyFailN = 3;

    @Builder.Default
    @Column(name = "daily_X", nullable = false)
    private int dailyX = 48;

    @Builder.Default
    @Column(name = "weekly_fail_N", nullable = false)
    private int weeklyFailN = 12;

    @Builder.Default
    @Column(name = "weekly_X", nullable = false)
    private int weeklyX = 48 * 7;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void touchUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
