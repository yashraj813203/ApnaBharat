package com.ab.responseDto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiIngestionLogResponseDto {

	private Long id;
	private String source;
	private String apiUrl;
	private String fetchType;
	private String status;
	private Integer fetchedRecords;
	private Integer failedRecords;
	private String errorMessage;
	private Instant startedAt;
	private Instant completedAt;
	private String remarks;
}
