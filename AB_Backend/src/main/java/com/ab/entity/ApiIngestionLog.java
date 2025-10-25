package com.ab.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_ingestion_logs", indexes = { @Index(name = "idx_ingestion_source", columnList = "source"),
		@Index(name = "idx_ingestion_status", columnList = "status"),
		@Index(name = "idx_ingestion_started_at", columnList = "started_at") })
public class ApiIngestionLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// -------------------------------------------
	// 1️⃣ BASIC INFO
	// -------------------------------------------

	@Column(name = "source", nullable = false, length = 150)
	private String source;
	// Example: "MGNREGA District Monthly Performance API"

	@Column(name = "api_url", columnDefinition = "TEXT")
	private String apiUrl;
	// The endpoint that was called

	@Column(name = "fetch_type", nullable = false, length = 20)
	private String fetchType;
	// Example: "INITIAL_LOAD", "SCHEDULED_REFRESH", "ON_DEMAND"

	// -------------------------------------------
	// 2️⃣ STATUS TRACKING
	// -------------------------------------------

	@Column(name = "status", nullable = false, length = 20)
	private String status;
	// "SUCCESS", "FAILURE", "PARTIAL"

	@Column(name = "fetched_records")
	private Integer fetchedRecords;

	@Column(name = "failed_records")
	private Integer failedRecords;

	@Column(name = "error_message", columnDefinition = "TEXT")
	private String errorMessage;

	// -------------------------------------------
	// 3️⃣ TIMING & AUDIT
	// -------------------------------------------

	@CreationTimestamp
	@Column(name = "started_at", nullable = false, updatable = false)
	private Instant startedAt;

	@Column(name = "completed_at")
	private Instant completedAt;

	// -------------------------------------------
	// 4️⃣ OPTIONAL METADATA
	// -------------------------------------------

	@Column(name = "remarks", columnDefinition = "TEXT")
	private String remarks;
}
