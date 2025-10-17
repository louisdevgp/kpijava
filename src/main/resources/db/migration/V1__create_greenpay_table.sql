CREATE TABLE IF NOT EXISTS goinsight_greenpay_record (
  terminal_sn       VARCHAR(64)  NOT NULL,
  merchant          VARCHAR(128) NULL,
  model             VARCHAR(64)  NULL,
  `status`          VARCHAR(32)  NULL,
  printer           VARCHAR(32)  NULL,
  location_raw      VARCHAR(64)  NULL,
  latitude          DOUBLE       NULL,
  longitude         DOUBLE       NULL,
  geofence          VARCHAR(64)  NULL,
  battery_healthy   VARCHAR(64)  NULL,
  offline_duration  VARCHAR(64)  NULL,
  mobile_carrier    VARCHAR(64)  NULL,
  iccid             VARCHAR(64)  NULL,
  `signal`          VARCHAR(64)  NULL,      -- mot réservé → backticks
  updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (terminal_sn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- si votre MariaDB/MySQL ne supporte pas IF NOT EXISTS pour les index,
-- créez-les “simples” (ignore l’erreur si l’index existe déjà) :
CREATE INDEX idx_greenpay_merchant ON goinsight_greenpay_record (merchant);
CREATE INDEX idx_greenpay_iccid    ON goinsight_greenpay_record (iccid);
CREATE INDEX idx_greenpay_model    ON goinsight_greenpay_record (model);
