package com.example.paxapi.kpi.config;

import com.example.paxapi.kpi.domain.KpiPolicy;
import com.example.paxapi.kpi.repo.KpiPolicyRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KpiBootstrap implements CommandLineRunner {

    private final KpiPolicyRepo policyRepo;

    @Override
    public void run(String... args) {
        if (policyRepo.count() == 0) {
            KpiPolicy def = KpiPolicy.builder()
                    .name("DEFAULT")
                    .useInternet(true)
                    .useTpeOn(true)
                    .useGeofence(true)
                    .useBattery(true)
                    .usePrinter(true)
                    .usePaper(true)
                    .dailyFailN(3)
                    .dailyX(48)
                    .weeklyFailN(12)
                    .weeklyX(336)
                    .build();
            def = policyRepo.save(def);
            log.info("Inserted default KPI policy id={}", def.getId());
        }
    }
}
