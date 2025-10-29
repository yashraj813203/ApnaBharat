package com.ab.config;

import com.ab.requestDto.MgnregaApiFilterRequestDto;
import com.ab.service.MgnregaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final MgnregaDataService mgnregaDataService;

    @Value("${mgnrega.api.base-url}")
    private String baseUrl;

    @Value("${mgnrega.api.resource-id}")
    private String resourceId;

    @Value("${mgnrega.api.api-key}")
    private String apiKey;

    @Value("${mgnrega.api.format:json}")
    private String format;

    @Value("${mgnrega.api.limit:10}")
    private int limit;

    // 🕒 Run every 12 hours (adjust as needed)
    @Scheduled(cron = "0 0 */12 * * *")
    public void scheduledDataRefresh() {
        log.info("🕓 Scheduled MGNREGA data refresh triggered...");

        MgnregaApiFilterRequestDto req = MgnregaApiFilterRequestDto.builder()
                .apiKey(apiKey)
                .format(format)
                .limit(limit)
                .offset(0)
                .stateName("Madhya Pradesh")  // ✅ optional example
                .finYear("2024-2025")        // ✅ example
                .build();

        log.info("🌍 Sending request to refresh MGNREGA data from configured API...");
        mgnregaDataService.refreshDataFromGovApi(req);
    }
}
