package com.ab.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to send filter parameters to the Government MGNREGA API.
 * All fields are optional except apiKey.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MgnregaApiFilterRequestDto {
	
	private String baseUrl;
	private String resourceId;

    // 🔐 API Key (Required)
    private String apiKey;

    // 🌍 Output format (json/xml/csv)
    private String format;

    // 📄 Pagination parameters
    private Integer limit;
    private Integer offset;

    // 🎯 Filter options for API
    private String stateName;
    private String finYear;
}
