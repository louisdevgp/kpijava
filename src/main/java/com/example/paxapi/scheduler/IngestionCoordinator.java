package com.example.paxapi.scheduler;

import com.example.paxapi.goinsight.service.GoinsightGreenpayIngestionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngestionCoordinator {

  private final GoinsightGreenpayIngestionService ingestion;

  @Value("${goinsight.greenpay.ingestion.max-run-minutes:25}")
  private int maxRunMinutes;

  private volatile boolean running = false;
  private volatile Instant startedAt = null;
  private volatile String reason = null;
  private volatile Integer lastCount = null;
  private volatile Boolean lastSuccess = null;

  /** État lisible par l’API. */
  @Getter
  public static class Status {
    private final boolean running;
    private final String reason;
    private final long elapsedSeconds;
    private final Integer lastCount;
    private final Boolean lastSuccess;

    public Status(boolean running, String reason, long elapsedSeconds, Integer lastCount, Boolean lastSuccess) {
      this.running = running;
      this.reason = reason;
      this.elapsedSeconds = elapsedSeconds;
      this.lastCount = lastCount;
      this.lastSuccess = lastSuccess;
    }
  }

  public Status status() {
    long elapsed = (startedAt == null) ? 0 : Duration.between(startedAt, Instant.now()).getSeconds();
    return new Status(running, reason, elapsed, lastCount, lastSuccess);
  }

  /** Réinitialise le verrou si planté. */
  public void resetLock() {
    log.warn("Manual lock reset requested.");
    running = false;
    startedAt = null;
    reason = null;
  }

  /** Lance une ingestion si pas déjà en cours. */
  public boolean trigger(String triggerReason, boolean full, boolean force) {
    // Anti chevauchement simple
    if (running) {
      long elapsedMin = (startedAt == null) ? 0 : Duration.between(startedAt, Instant.now()).toMinutes();
      if (!force && elapsedMin < maxRunMinutes) {
        log.info("Ingestion skipped (already running, elapsed={} min, reason={})", elapsedMin, reason);
        return false;
      }
      if (force) {
        log.warn("Forcing ingestion while previous run marked running (elapsed={} min).", elapsedMin);
      }
    }

    running = true;
    startedAt = Instant.now();
    reason = triggerReason;
    lastCount = null;
    lastSuccess = null;

    try {
      int count = ingestion.ingestAll(full);
      lastCount = count;
      lastSuccess = true;
      log.info("Ingestion finished (reason={}, full={}, count={})", triggerReason, full, count);
      return true;
    } catch (Exception ex) {
      lastSuccess = false;
      log.error("Ingestion failed (reason={}, full={})", triggerReason, full, ex);
      return false;
    } finally {
      running = false;
    }
  }

  /** Optionnel: petit run de chauffe au boot. */
  public void warmKick() {
    try {
      trigger("warm-kick", false, false);
    } catch (Exception ignored) {
      // on ignore au boot
    }
  }
}
