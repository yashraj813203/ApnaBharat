package com.ab.serviceImpl;

import com.ab.entity.ApiIngestionLog;
import com.ab.repository.ApiIngestionLogRepository;
import com.ab.service.ApiIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.ab.util.Constants.*; // ✅ Import all constants

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApiIngestionServiceImpl implements ApiIngestionService {

	private final ApiIngestionLogRepository logRepository;

	// -----------------------------------------
	// 1️⃣ Start Ingestion Log
	// -----------------------------------------
	@Override
	public ApiIngestionLog startIngestion(String source, String apiUrl, String fetchType) {
		ApiIngestionLog logEntry = ApiIngestionLog.builder().source(source).apiUrl(apiUrl).fetchType(fetchType)
				.status(STATUS_RUNNING) // ✅ using constant
				.fetchedRecords(0).failedRecords(0).startedAt(Instant.now())
				.remarks("Ingestion started at " + Instant.now()).build();

		ApiIngestionLog saved = logRepository.save(logEntry);
		log.info("🟡 Ingestion started for source [{}] at {}", source, saved.getStartedAt());
		return saved;
	}

	// -----------------------------------------
	// 2️⃣ Complete Ingestion Log
	// -----------------------------------------
	@Override
	public void completeIngestion(ApiIngestionLog logEntry, String status, int fetched, int failed, String remarks) {
		logEntry.setStatus(status);
		logEntry.setFetchedRecords(fetched);
		logEntry.setFailedRecords(failed);
		logEntry.setCompletedAt(Instant.now());
		logEntry.setRemarks(remarks);

		logRepository.save(logEntry);

		// ✅ Use constants to match messages
		if (STATUS_SUCCESS.equals(status)) {
			log.info("🟢 Ingestion SUCCESS for [{}] — Fetched: {}, Failed: {}", logEntry.getSource(), fetched, failed);
		} else if (STATUS_FAILURE.equals(status)) {
			log.error("🔴 Ingestion FAILURE for [{}] — Fetched: {}, Failed: {}", logEntry.getSource(), fetched, failed);
		} else if (STATUS_PARTIAL.equals(status)) {
			log.warn("🟠 Ingestion PARTIAL for [{}] — Fetched: {}, Failed: {}", logEntry.getSource(), fetched, failed);
		}
	}

	// -----------------------------------------
	// 3️⃣ Get Latest Log by Source
	// -----------------------------------------
	@Override
	@Transactional(readOnly = true)
	public ApiIngestionLog getLatestLog(String source) {
		ApiIngestionLog logEntry = logRepository.findTop1BySourceOrderByStartedAtDesc(source);
		if (logEntry != null) {
			log.info("ℹ️ Latest ingestion log fetched for [{}]: status={}", source, logEntry.getStatus());
		}
		return logEntry;
	}

	// -----------------------------------------
	// 4️⃣ Get Recent Logs
	// -----------------------------------------
	@Override
	@Transactional(readOnly = true)
	public List<ApiIngestionLog> getRecentLogs(String source, int limit) {
		List<ApiIngestionLog> logs = logRepository.findTop10BySourceOrderByStartedAtDesc(source);
		log.info("📄 Recent {} ingestion logs fetched for [{}]", logs.size(), source);
		return logs;
	}

	// -----------------------------------------
	// 5 smart scheduler overlap check
	// -----------------------------------------
	@Override
	@Transactional(readOnly = true)
	public boolean isIngestionRunning(String source) {
		ApiIngestionLog latest = logRepository.findTop1BySourceOrderByStartedAtDesc(source);
		return latest != null && STATUS_RUNNING.equals(latest.getStatus());
	}

}
