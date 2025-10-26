package com.example.paxapi.goinsight.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vw_greenpay_timeline")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GreenpayTimelineView {

  @EmbeddedId
  private Id id;

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

  @Embeddable
  @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
  public static class Id implements Serializable {
    @Column(name = "terminal_sn", length = 64, nullable = false)
    private String terminalSn;

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;
  }
}
