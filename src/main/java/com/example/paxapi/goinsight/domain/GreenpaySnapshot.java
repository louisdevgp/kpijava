package com.example.paxapi.goinsight.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "goinsight_greenpay_snapshot",
        indexes = {
            @Index(name = "idx_snap_sn", columnList = "terminal_sn"),
            @Index(name = "idx_snap_captured", columnList = "captured_at"),
            @Index(name = "idx_snap_sn_time", columnList = "terminal_sn,captured_at"),
            @Index(name = "idx_snap_time", columnList = "captured_at")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_snapshot_sn_bucket", columnNames = {"terminal_sn", "captured_at"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GreenpaySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "terminal_sn", length = 64, nullable = false)
    private String terminalSn;

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;  // bucket 30 min (Africa/Abidjan)

    @Column(name = "merchant", length = 128)
    private String merchant;

    @Column(name = "model", length = 64)
    private String model;

    @Column(name = "status", length = 64)
    private String status;

    @Column(name = "printer", length = 64)
    private String printer;

    @Column(name = "location_raw", length = 128)
    private String locationRaw;

    private Double latitude;
    private Double longitude;

    @Column(name = "geofence", length = 64)
    private String geofence;

    @Column(name = "battery_healthy", length = 64)
    private String batteryHealthy;

    @Column(name = "offline_duration", length = 64)
    private String offlineDuration;

    @Column(name = "mobile_carrier", length = 64)
    private String mobileCarrier;

    @Column(name = "iccid", length = 64)
    private String iccid;

    @Column(name = "`signal`", length = 64)
    private String signal;

    @Column(name = "is_charging")
    private Boolean isCharging;

    @Column(name = "battery_rate_avg", precision = 5, scale = 2)
    private BigDecimal batteryRateAvg;

    @Column(name = "battery_rate_consume", precision = 5, scale = 2)
    private BigDecimal batteryRateConsume;
}
