package com.ab.serviceImpl;

import com.ab.entity.*;
import com.ab.repository.*;
import com.ab.requestDto.MgnregaApiFilterRequestDto;
import com.ab.responseDto.MgnregaApiResponseDto;
import com.ab.responseDto.MgnregaMonthlyRecordDto;
import com.ab.responseDto.MgnregaMonthlyStatResponseDto;
import com.ab.service.MgnregaDataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MgnregaDataServiceImpl implements MgnregaDataService {

    private final WebClient.Builder webClientBuilder;
    private final MgnregaMonthlyStatRepository statRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final FinancialYearRepository financialYearRepository;

    @Value("${mgnrega.api.base-url}")
    private String baseUrl;

    @Value("${mgnrega.api.resource-id}")
    private String resourceId;

    @Value("${mgnrega.api.api-key}")
    private String apiKey;

    @Value("${mgnrega.api.format}")
    private String apiFormat;

    @Value("${mgnrega.api.limit}")
    private int apiLimit;

    @Value("${mgnrega.api.offset:${MGNREGA_API_OFFSET:0}}")
    private int apiOffset;


    // =============================================================
    // 1️⃣ Fetch and store data from Govt API
    // =============================================================
    @Override
    @Transactional
    public void refreshDataFromGovApi(MgnregaApiFilterRequestDto req) {
        String url = buildApiUrl(req);
        log.info("🌍 Fetching MGNREGA data from: {}", url);

        try {
            // Step 1️⃣: Fetch raw JSON string
        	String responseBody = webClientBuilder.build()
        	        .get()
        	        .uri(url)
        	        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/122.0.0.0 Safari/537.36")
        	        .retrieve()
        	        .bodyToMono(String.class)
        	        .block();

            if (responseBody == null || responseBody.isBlank()) {
                log.warn("⚠️ Empty response body received from API.");
                return;
            }

            // Step 2️⃣: Debug log first 500 chars
            log.info("🌐 Raw API Response (first 500 chars): {}", 
                     responseBody.substring(0, Math.min(responseBody.length(), 500)));

            // Step 3️⃣: Parse root JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode recordsNode = rootNode.path("records");

            if (recordsNode.isMissingNode() || !recordsNode.isArray()) {
                log.warn("⚠️ 'records' field missing or not an array in API response.");
                return;
            }

            // Step 4️⃣: Deserialize records
            List<MgnregaMonthlyRecordDto> records = mapper.readValue(
                    recordsNode.toString(),
                    new TypeReference<List<MgnregaMonthlyRecordDto>>() {}
            );

            log.info("✅ Retrieved {} records from API.", records.size());
            if (records.isEmpty()) {
                log.warn("⚠️ API returned an empty records list — possible filter mismatch.");
                return;
            }

            int inserted = 0;

            // Step 5️⃣: Process and persist each record
            for (MgnregaMonthlyRecordDto dto : records) {
            	if (dto.getMonth() == null || dto.getMonth().isBlank()) {
            	    log.warn("⚠️ Skipping missing month for district: {}", dto.getDistrict_name());
            	    continue;
            	}

            	// Normalize month
            	String normalizedMonth = dto.getMonth().trim().toUpperCase();

            	// Updated valid month pattern — includes TOTAL / ANNUAL / APRIL-2024 types
            	if (!isValidMonth(normalizedMonth)) {
            	    log.debug("ℹ️ Allowing month value '{}' for district {}", normalizedMonth, dto.getDistrict_name());
            	}

            	// Store normalized month back into DTO
            	dto.setMonth(normalizedMonth);


                District district = getOrCreateDistrict(dto);
                int monthNo = mapMonth(dto.getMonth());
                String financialYearStr = dto.getFin_year();

                FinancialYear finYearEntity = getOrCreateFinancialYear(district, financialYearStr);

                boolean exists = statRepository.existsByFinancialYearAndMonthNo(finYearEntity, monthNo);
                if (exists) continue;

                MgnregaMonthlyStat entity = mapToEntity(dto, finYearEntity, monthNo);
                statRepository.save(entity);
                inserted++;
            }

            log.info("💾 Successfully inserted {} new records (existing ones skipped).", inserted);

        } catch (Exception e) {
            log.error("❌ Error while refreshing MGNREGA data: {}", e.getMessage(), e);
            throw new RuntimeException("Error while refreshing data: " + e.getMessage(), e);
        }
    }


    // =============================================================
    // 2️⃣ Get latest data for a district
    // =============================================================
    @Override
    public List<MgnregaMonthlyStatResponseDto> getLatestDataByDistrict(String districtName) {
        List<MgnregaMonthlyStat> stats = statRepository.findLatestByDistrictName(districtName);
        return stats.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // =============================================================
    // 3️⃣ Get filtered data (for user UI)
    // =============================================================
    @Override
    public List<MgnregaMonthlyStatResponseDto> getFilteredData(
            String stateName, String districtName, String finYear, int page, int size) {

        List<MgnregaMonthlyStat> stats = statRepository.findByFilters(stateName, districtName, finYear);
        return stats.stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // =============================================================
    // 4️⃣ Count total records
    // =============================================================
    @Override
    public long countAllRecords() {
        return statRepository.count();
    }

    // =============================================================
    // 🔧 Utility & Helper Methods
    // =============================================================

    private String buildApiUrl(MgnregaApiFilterRequestDto req) {
        String baseUrl = req.getBaseUrl();
        String resourceId = req.getResourceId();

        // Normalize and encode filter values
        String encodedStateName = "";
        String encodedFinYear = "";

        if (req.getStateName() != null && !req.getStateName().isBlank()) {
            encodedStateName = URLEncoder.encode(req.getStateName().trim().toUpperCase(), StandardCharsets.UTF_8);
        }

        if (req.getFinYear() != null && !req.getFinYear().isBlank()) {
            encodedFinYear = URLEncoder.encode(req.getFinYear().trim(), StandardCharsets.UTF_8);
        }

        // Build URL dynamically and safely
        StringBuilder sb = new StringBuilder(baseUrl);
        if (!baseUrl.endsWith("/")) sb.append("/");
        sb.append("resource/").append(resourceId)
          .append("?api-key=").append(req.getApiKey())
          .append("&format=").append(req.getFormat())
          .append("&limit=").append(req.getLimit())
          .append("&offset=").append(req.getOffset());

        if (!encodedStateName.isEmpty())
            sb.append("&filters[state_name]=").append(encodedStateName);

        if (!encodedFinYear.isEmpty())
            sb.append("&filters[fin_year]=").append(encodedFinYear);

        String finalUrl = sb.toString();
        log.info("🔗 Final API URL: {}", finalUrl);
        return finalUrl;
    }



    private District getOrCreateDistrict(MgnregaMonthlyRecordDto dto) {
        Optional<State> stateOpt = stateRepository.findByStateCode(dto.getState_code());
        State state = stateOpt.orElseGet(() ->
                stateRepository.save(State.builder()
                        .stateCode(dto.getState_code())
                        .stateName(dto.getState_name())
                        .build())
        );

        return districtRepository.findByDistrictCode(dto.getDistrict_code())
                .orElseGet(() ->
                        districtRepository.save(District.builder()
                                .districtCode(dto.getDistrict_code())
                                .districtName(dto.getDistrict_name())
                                .state(state)
                                .build())
                );
    }

    private FinancialYear getOrCreateFinancialYear(District district, String finYearStr) {
        return financialYearRepository.findByDistrictAndFinancialYear(district, finYearStr)
                .orElseGet(() -> financialYearRepository.save(
                        FinancialYear.builder()
                                .district(district)
                                .financialYear(finYearStr)
                                .build()
                ));
    }

    private boolean isValidMonth(String month) {
        if (month == null) return false;
        month = month.trim().toUpperCase();

        // Accept standard month names and common API variants
        return month.matches("^(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|TOTAL|ANNUAL|.*\\d{4})$");
    }


    private int mapMonth(String month) {
        try {
            return Month.valueOf(month.substring(0, 3).toUpperCase()).getValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private MgnregaMonthlyStat mapToEntity(MgnregaMonthlyRecordDto dto, FinancialYear finYearEntity, int monthNo) {
        return MgnregaMonthlyStat.builder()
                .financialYear(finYearEntity)
                .monthNo(monthNo)
                .approvedLabourBudget(dto.getApproved_Labour_Budget())
                .averageWageRate(dto.getAverage_Wage_rate_per_day_per_person())
                .averageDaysOfEmployment(dto.getAverage_days_of_employment_provided_per_Household())
                .differentlyAbledPersonsWorked(dto.getDifferently_abled_persons_worked())
                .materialAndSkilledWages(dto.getMaterial_and_skilled_Wages())
                .numberOfCompletedWorks(dto.getNumber_of_Completed_Works())
                .numberOfGPsWithNilExp(dto.getNumber_of_GPs_with_NIL_exp())
                .numberOfOngoingWorks(dto.getNumber_of_Ongoing_Works())
                .persondaysCentralLiability(dto.getPersondays_of_Central_Liability_so_far())
                .scPersondays(dto.getSc_persondays())
                .stPersondays(dto.getSt_persondays())
                .totalExpenditure(dto.getTotal_Exp())
                .totalAdminExpenditure(dto.getTotal_Adm_Expenditure())
                .totalHouseholdsWorked(dto.getTotal_Households_Worked())
                .totalIndividualsWorked(dto.getTotal_Individuals_Worked())
                .totalActiveJobcards(dto.getTotal_No_of_Active_Job_Cards())
                .totalActiveWorkers(dto.getTotal_No_of_Active_Workers())
                .totalHHsCompleted100Days(dto.getTotal_No_of_HHs_completed_100_Days_of_Wage_Employment())
                .totalJobcardsIssued(dto.getTotal_No_of_JobCards_issued())
                .totalWorkers(dto.getTotal_No_of_Workers())
                .totalWorksTakenUp(dto.getTotal_No_of_Works_Takenup())
                .wages(dto.getWages())
                .womenPersondays(dto.getWomen_Persondays())
                .percentCategoryBWorks(dto.getPercent_of_Category_B_Works())
                .percentExpAgriAllied(dto.getPercent_of_Expenditure_on_Agriculture_Allied_Works())
                .percentNrmExpenditure(dto.getPercent_of_NRM_Expenditure())
                .percentPaymentsWithin15Days(dto.getPercentage_payments_gererated_within_15_days())
                .remarks(dto.getRemarks())
                .build();
    }

    private MgnregaMonthlyStatResponseDto toResponseDto(MgnregaMonthlyStat entity) {
        District district = entity.getFinancialYear().getDistrict();
        State state = district.getState();

        return MgnregaMonthlyStatResponseDto.builder()
                .stateCode(state.getStateCode())
                .stateName(state.getStateName())
                .districtCode(district.getDistrictCode())
                .districtName(district.getDistrictName())
                .financialYear(entity.getFinancialYear().getFinancialYear())
                .monthName(Month.of(entity.getMonthNo()).name())
                .approvedLabourBudget(entity.getApprovedLabourBudget())
                .averageWageRate(entity.getAverageWageRate())
                .averageDaysOfEmployment(entity.getAverageDaysOfEmployment())
                .differentlyAbledPersonsWorked(entity.getDifferentlyAbledPersonsWorked())
                .materialAndSkilledWages(entity.getMaterialAndSkilledWages())
                .numberOfCompletedWorks(entity.getNumberOfCompletedWorks())
                .numberOfGPsWithNilExp(entity.getNumberOfGPsWithNilExp())
                .numberOfOngoingWorks(entity.getNumberOfOngoingWorks())
                .persondaysCentralLiability(entity.getPersondaysCentralLiability())
                .scPersondays(entity.getScPersondays())
                .stPersondays(entity.getStPersondays())
                .totalExpenditure(entity.getTotalExpenditure())
                .totalAdminExpenditure(entity.getTotalAdminExpenditure())
                .totalHouseholdsWorked(entity.getTotalHouseholdsWorked())
                .totalIndividualsWorked(entity.getTotalIndividualsWorked())
                .totalActiveJobcards(entity.getTotalActiveJobcards())
                .totalActiveWorkers(entity.getTotalActiveWorkers())
                .totalHHsCompleted100Days(entity.getTotalHHsCompleted100Days())
                .totalJobcardsIssued(entity.getTotalJobcardsIssued())
                .totalWorkers(entity.getTotalWorkers())
                .totalWorksTakenUp(entity.getTotalWorksTakenUp())
                .wages(entity.getWages())
                .womenPersondays(entity.getWomenPersondays())
                .percentCategoryBWorks(entity.getPercentCategoryBWorks())
                .percentExpAgriAllied(entity.getPercentExpAgriAllied())
                .percentNrmExpenditure(entity.getPercentNrmExpenditure())
                .percentPaymentsWithin15Days(entity.getPercentPaymentsWithin15Days())
                .remarks(entity.getRemarks())
                .build();
    }
}
