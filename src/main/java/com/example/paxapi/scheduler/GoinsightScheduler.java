package com.example.paxapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoinsightScheduler {

  private final IngestionCoordinator coordinator;

  @Value("${goinsight.greenpay.ingestion.enabled:true}")
  private boolean enabled;

  /** 12:00 et 12:30 */
  @Scheduled(cron = "${goinsight.greenpay.ingestion.cron1}")
  public void cron12() {
    if (!enabled) return;
    log.info("scheduler cron12 fired");
    coordinator.trigger("cron12", true, false);
  }

  /** 13:00 */
  @Scheduled(cron = "${goinsight.greenpay.ingestion.cron2}")
  public void cron13() {
    if (!enabled) return;
    log.info("scheduler cron13 fired");
    coordinator.trigger("cron13", true, false);
  }

  /** 14:00 et 15:00 */
  @Scheduled(cron = "${goinsight.greenpay.ingestion.cron3}")
  public void cron14_15() {
    if (!enabled) return;
    log.info("scheduler cron14_15 fired");
    coordinator.trigger("cron14_15", true, false);
  }

  /** 17:00–19:00 : à chaque heure */
  @Scheduled(cron = "${goinsight.greenpay.ingestion.cron4}")
  public void cron17_19() {
    if (!enabled) return;
    log.info("scheduler cron17_19 fired");
    coordinator.trigger("cron17_19", true, false);
  }

  /** Optionnel : petit run auto au boot (désactive si tu veux). */
  @EventListener(ApplicationReadyEvent.class)
  public void warmKick() {
    // Active si tu veux un premier run de chauffe au boot
    // coordinator.warmKick();
  }
}
