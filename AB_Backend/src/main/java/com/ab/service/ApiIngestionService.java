package com.ab.service;

import com.ab.entity.ApiIngestionLog;
import java.util.List;

public interface ApiIngestionService {

	/**
	 * Create a new ingestion log entry when process starts.
	 */
	ApiIngestionLog startIngestion(String source, String apiUrl, String fetchType);

	/**
	 * Update an existing ingestion log upon completion.
	 */
	void completeIngestion(ApiIngestionLog log, String status, int fetched, int failed, String remarks);

	/**
	 * Fetch the latest log for a given data source.
	 */
	ApiIngestionLog getLatestLog(String source);

	/**
	 * Fetch last N ingestion logs for dashboard history.
	 */
	List<ApiIngestionLog> getRecentLogs(String source, int limit);
	
	boolean isIngestionRunning(String source);
}
