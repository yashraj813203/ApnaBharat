package com.ab.config;

import com.ab.service.ApiIngestionService;
import com.ab.service.MgnregaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.ab.util.Constants.SOURCE_MGNREGA;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final MgnregaDataService mgnregaDataService;
    private final ApiIngestionService ingestionService;

    // Runs once every 24 hours
    @Scheduled(cron = "0 0 2 * * ?") // 2:00 AM daily
    public void refreshMgnregaData() {

        log.info("Scheduler triggered", SOURCE_MGNREGA);

        // ✅ Step 1: Check if ingestion is already running
        if (ingestionService.isIngestionRunning(SOURCE_MGNREGA)) {
            log.warn("Previous ingestion still RUNNING — skipping this scheduled execution.");
            return;
        }

        // ✅ Step 2: Safe to start a new ingestion
        try {
            log.info("Scheduled MGNREGA data refresh started...");
            mgnregaDataService.refreshAllData();
            log.info("Scheduled MGNREGA data refresh completed.");
        } catch (Exception e) {
            log.error("Scheduled ingestion failed", SOURCE_MGNREGA, e.getMessage());
        }
    }
}
