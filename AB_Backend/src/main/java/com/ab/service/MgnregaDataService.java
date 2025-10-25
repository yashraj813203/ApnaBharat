package com.ab.service;

import com.ab.requestDto.MgnregaMonthlyStatRequestDto;
import com.ab.responseDto.MgnregaMonthlyStatResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MgnregaDataService {

	/**
	 * Save or update a monthly MGNREGA record for a district.
	 */
	void saveOrUpdateMonthlyStat(MgnregaMonthlyStatRequestDto dto);

	/**
	 * Fetch paginated stats filtered by state name.
	 */
	Page<MgnregaMonthlyStatResponseDto> getStatsByState(String stateName, Pageable pageable);

	/**
	 * Fetch paginated stats filtered by district name.
	 */
	Page<MgnregaMonthlyStatResponseDto> getStatsByDistrict(String districtName, Pageable pageable);

	/**
	 * Refresh and re-ingest all data from external API.
	 */
	void refreshAllData();

	/**
	 * Get total record count (for dashboard display).
	 */
	long countAllRecords();
}
