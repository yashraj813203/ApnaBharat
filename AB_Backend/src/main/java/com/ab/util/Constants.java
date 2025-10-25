package com.ab.util;

public class Constants {

    // ✅ Cache Names
    public static final String CACHE_STATS_BY_STATE = "statsByState";
    public static final String CACHE_STATS_BY_DISTRICT = "statsByDistrict";

    // ✅ API Ingestion Status
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";
    public static final String STATUS_PARTIAL = "PARTIAL";
    public static final String STATUS_RUNNING = "RUNNING";

    // ✅ Fetch Types
    public static final String FETCH_INITIAL_LOAD = "INITIAL_LOAD";
    public static final String FETCH_SCHEDULED_REFRESH = "SCHEDULED_REFRESH";
    public static final String FETCH_ON_DEMAND = "ON_DEMAND";

    // ✅ Source Identifiers
    public static final String SOURCE_MGNREGA = "MGNREGA District Monthly Performance";

    // ✅ Other Common Constants
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String TIME_ZONE = "Asia/Kolkata";
}
