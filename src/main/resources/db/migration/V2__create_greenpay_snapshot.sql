CREATE TABLE IF NOT EXISTS goinsight_greenpay_snapshot (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  terminal_sn      VARCHAR(64)  NOT NULL,
  captured_at      DATETIME     NOT NULL, -- bucket 30 min (Africa/Abidjan)
  merchant         VARCHAR(128) NULL,
  model            VARCHAR(64)  NULL,
  `status`         VARCHAR(32)  NULL,
  printer          VARCHAR(32)  NULL,
  location_raw     VARCHAR(64)  NULL,
  latitude         DOUBLE       NULL,
  longitude        DOUBLE       NULL,
  geofence         VARCHAR(64)  NULL,
  battery_healthy  VARCHAR(64)  NULL,
  offline_duration VARCHAR(64)  NULL,
  mobile_carrier   VARCHAR(64)  NULL,
  iccid            VARCHAR(64)  NULL,
  `signal`         VARCHAR(64)  NULL,
  PRIMARY KEY (id),
  UNIQUE KEY ux_greenpay_snap_terminal_bucket (terminal_sn, captured_at),
  KEY idx_greenpay_snap_terminal_time (terminal_sn, captured_at),
  KEY idx_greenpay_snap_captured_at (captured_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
