package com.example.paxapi.goinsight.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "goinsight_greenpay_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GreenpayRecord {

  @Id
  @Column(name = "terminal_sn", length = 64, nullable = false)
  private String terminalSn;              // PK

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

  // ⚠️ dans ta base la colonne s'appelle `offline_duration`
  @Column(name = "offline_duration", length = 64)
  private String offlineDuration;

  @Column(name = "mobile_carrier", length = 64)
  private String mobileCarrier;

  @Column(name = "iccid", length = 64)
  private String iccid;

  // MariaDB/MySQL ont un mot-clé SIGNAL -> on quote le nom
  @Column(name = "`signal`", length = 64)
  private String signal;

  // === NOUVELLES COLONNES BATTERIE ===
  @Column(name = "is_charging")
  private Boolean isCharging;

  @Column(name = "battery_rate_avg", precision = 5, scale = 2)
  private BigDecimal batteryRateAvg;

  @Column(name = "battery_rate_consume", precision = 5, scale = 2)
  private BigDecimal batteryRateConsume;

  // Géré par la DB (DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;
}
