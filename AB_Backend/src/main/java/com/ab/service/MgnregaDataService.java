package com.ab.service;

import com.ab.requestDto.MgnregaApiFilterRequestDto;
import com.ab.responseDto.MgnregaMonthlyStatResponseDto;
import java.util.List;

public interface MgnregaDataService {

    /**
     * Fetches data from Government API based on filters and stores new records in DB.
     */
    void refreshDataFromGovApi(MgnregaApiFilterRequestDto filterRequest);

    /**
     * Returns latest data for a specific district.
     */
    List<MgnregaMonthlyStatResponseDto> getLatestDataByDistrict(String districtName);

    /**
     * Returns filtered data (by state, district, fin year) — for user filters in UI.
     */
    List<MgnregaMonthlyStatResponseDto> getFilteredData(String stateName, String districtName, String finYear, int page, int size);

    /**
     * Counts total records in our database.
     */
    long countAllRecords();
}
