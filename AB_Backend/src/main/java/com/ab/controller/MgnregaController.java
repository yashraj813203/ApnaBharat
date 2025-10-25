package com.ab.controller;

import com.ab.responseDto.MgnregaMonthlyStatResponseDto;
import com.ab.service.MgnregaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MGNREGA Data APIs", description = "Fetch and refresh district/state-wise performance data")
@RestController
@RequestMapping("/api/v1/mgnrega")
@RequiredArgsConstructor
public class MgnregaController {

    private final MgnregaDataService mgnregaDataService;

    // ------------------------------------------
    // 1️⃣ Fetch paginated stats by State
    // ------------------------------------------
    @Operation(summary = "Get MGNREGA statistics by State name", description = "Fetch paginated monthly data for a given state.")
    @GetMapping("/state/{stateName}")
    public ResponseEntity<Page<MgnregaMonthlyStatResponseDto>> getByState(
            @PathVariable String stateName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MgnregaMonthlyStatResponseDto> response = mgnregaDataService.getStatsByState(stateName, pageable);
        return ResponseEntity.ok(response);
    }

    // ------------------------------------------
    // 2️⃣ Fetch paginated stats by District
    // ------------------------------------------
    @Operation(summary = "Get MGNREGA statistics by District name", description = "Fetch paginated monthly data for a given district.")
    @GetMapping("/district/{districtName}")
    public ResponseEntity<Page<MgnregaMonthlyStatResponseDto>> getByDistrict(
            @PathVariable String districtName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MgnregaMonthlyStatResponseDto> response = mgnregaDataService.getStatsByDistrict(districtName, pageable);
        return ResponseEntity.ok(response);
    }

    // ------------------------------------------
    // 3️⃣ Refresh Data (calls API ingestion)
    // ------------------------------------------
    @Operation(summary = "Refresh MGNREGA data", description = "Fetches and updates latest data from government Open API.")
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshData() {
        mgnregaDataService.refreshAllData();
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("🟢 Data refresh started — check ingestion logs for status.");
    }

    // ------------------------------------------
    // 4️⃣ Count all records (for dashboard summary)
    // ------------------------------------------
    @Operation(summary = "Get total records", description = "Returns total number of MGNREGA records stored in database.")
    @GetMapping("/count")
    public ResponseEntity<Long> countRecords() {
        return ResponseEntity.ok(mgnregaDataService.countAllRecords());
    }
}
