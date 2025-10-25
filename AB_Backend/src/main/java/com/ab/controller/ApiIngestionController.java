package com.ab.controller;

import com.ab.entity.ApiIngestionLog;
import com.ab.service.ApiIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "API Ingestion Logs", description = "Monitor the status and history of data ingestion runs")
@RestController
@RequestMapping("/api/v1/ingestion-logs")
@RequiredArgsConstructor
public class ApiIngestionController {

    private final ApiIngestionService ingestionService;

    // ------------------------------------------
    // 1️⃣ Latest ingestion log for given source
    // ------------------------------------------
    @Operation(summary = "Get latest ingestion log", description = "Fetch the most recent ingestion run details for a given source.")
    @GetMapping("/latest")
    public ResponseEntity<ApiIngestionLog> getLatestLog(
            @RequestParam(defaultValue = "MGNREGA District Monthly Performance") String source) {
        ApiIngestionLog latestLog = ingestionService.getLatestLog(source);
        return ResponseEntity.ok(latestLog);
    }

    // ------------------------------------------
    // 2️⃣ Last 10 ingestion logs
    // ------------------------------------------
    @Operation(summary = "Get recent ingestion logs", description = "Fetch the last 10 ingestion logs for a given source.")
    @GetMapping("/recent")
    public ResponseEntity<List<ApiIngestionLog>> getRecentLogs(
            @RequestParam(defaultValue = "MGNREGA District Monthly Performance") String source) {
        List<ApiIngestionLog> logs = ingestionService.getRecentLogs(source, 10);
        return ResponseEntity.ok(logs);
    }
}
