package com.ab.controller;

import com.ab.requestDto.MgnregaApiFilterRequestDto;
import com.ab.service.MgnregaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mgnrega")
public class MgnregaController {

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


    @PostMapping("/refresh")
    public ResponseEntity<String> refreshData() {
        log.info("🛰️ Starting MGNREGA data refresh using environment configuration...");

        MgnregaApiFilterRequestDto req = MgnregaApiFilterRequestDto.builder()
                .baseUrl(baseUrl)
                .resourceId(resourceId)
                .apiKey(apiKey)
                .format(format)
                .limit(limit)
                .offset(0)
                .stateName("MADHYA PRADESH")        // ✅ try this one
                .finYear("2024-2025")     // ✅ available year
                .build();


        mgnregaDataService.refreshDataFromGovApi(req);
        return ResponseEntity.ok("✅ Data refresh started successfully.");
    }


    // Example: fetch filtered data
    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredData(
            @RequestParam(required = false) String stateName,
            @RequestParam(required = false) String districtName,
            @RequestParam(required = false) String finYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                mgnregaDataService.getFilteredData(stateName, districtName, finYear, page, size)
        );
    }

    @GetMapping("/district/{name}")
    public ResponseEntity<?> getLatestDistrictData(@PathVariable String name) {
        return ResponseEntity.ok(mgnregaDataService.getLatestDataByDistrict(name));
    }
}
