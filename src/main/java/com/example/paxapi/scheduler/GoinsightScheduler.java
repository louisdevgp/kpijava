package com.example.paxapi.scheduler;

import com.example.paxapi.goinsight.service.GoinsightGreenpayIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@ConditionalOnProperty(prefix = "goinsight.ingestion", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class GoinsightScheduler {

  private final GoinsightGreenpayIngestionService svc;

  // Fuseau CRON: UTC (les buckets restent en Africa/Abidjan dans le service)
  @Scheduled(cron = "${goinsight.ingestion.cron}", zone = "UTC")
  public void run() {
    try {
      int n = svc.ingestAll();
      log.info("Scheduled Goinsight ingestion (merged) done: {} terminals updated", n);
    } catch (Exception e) {
      log.error("Scheduled Goinsight ingestion failed", e);
    }
  }
}
