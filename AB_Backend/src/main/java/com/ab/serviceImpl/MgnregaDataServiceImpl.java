package com.ab.serviceImpl;

import com.ab.requestDto.MgnregaMonthlyStatRequestDto;
import com.ab.responseDto.MgnregaMonthlyStatResponseDto;
import com.ab.entity.*;
import com.ab.repository.*;
import com.ab.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import static com.ab.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MgnregaDataServiceImpl implements MgnregaDataService {

	private final MgnregaMonthlyStatRepository statRepository;
	private final DistrictRepository districtRepository;
	private final StateRepository stateRepository;
	private final ApiIngestionService ingestionService;
	private final WebClient webClient;

	@Value("${mgnrega.api.resource-id}")
	private String resourceId;

	@Value("${mgnrega.api.api-key}")
	private String apiKey;

	@Value("${mgnrega.api.format}")
	private String format;

	@Value("${mgnrega.api.limit}")
	private int limit;

	// -------------------------
	// 1️⃣ Save or Update a record
	// -------------------------
	@Override
	@Transactional
	@CacheEvict(value = { "statsByState", "statsByDistrict" }, allEntries = true)
	public void saveOrUpdateMonthlyStat(MgnregaMonthlyStatRequestDto dto) {

		Optional<District> districtOpt = districtRepository.findByDistrictCode(dto.getDistrictCode());
		if (districtOpt.isEmpty()) {
			log.warn("⚠️ District not found for code {}", dto.getDistrictCode());
			return;
		}
		District district = districtOpt.get();

		Optional<MgnregaMonthlyStat> existingOpt = statRepository.findByDistrictAndMonthNoAndFinancialYear(district,
				dto.getMonthNo(), dto.getFinancialYear());

		MgnregaMonthlyStat stat = existingOpt.orElseGet(() -> MgnregaMonthlyStat.builder().district(district)
				.monthNo(dto.getMonthNo()).financialYear(dto.getFinancialYear()).build());

		// Update fields
		stat.setHouseholdsDemanded(dto.getHouseholdsDemanded());
		stat.setHouseholdsProvided(dto.getHouseholdsProvided());
		stat.setPersonsDemanded(dto.getPersonsDemanded());
		stat.setPersonsProvided(dto.getPersonsProvided());
		stat.setPersondaysGenerated(dto.getPersondaysGenerated());
		stat.setAvgWageRate(dto.getAvgWageRate());
		stat.setTotalExpenditure(dto.getTotalExpenditure());
		stat.setWorksCompleted(dto.getWorksCompleted());
		stat.setScShare(dto.getScShare());
		stat.setStShare(dto.getStShare());
		stat.setWomenShare(dto.getWomenShare());

		statRepository.save(stat);
		log.info("✅ Saved/Updated record for District [{}] - Month {} FY {}", district.getDistrictName(),
				dto.getMonthNo(), dto.getFinancialYear());
	}

	// -------------------------
	// 2️⃣ Get stats by State (cached + paginated)
	// -------------------------
	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "statsByState", key = "#stateName + '_' + #pageable.pageNumber")
	public Page<MgnregaMonthlyStatResponseDto> getStatsByState(String stateName, Pageable pageable) {
		Page<MgnregaMonthlyStat> stats = statRepository.findAllByDistrict_State_StateNameIgnoreCase(stateName,
				pageable);
		return stats.map(this::mapToResponseDto);
	}

	// -------------------------
	// 3️⃣ Get stats by District (cached + paginated)
	// -------------------------
	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "statsByDistrict", key = "#districtName + '_' + #pageable.pageNumber")
	public Page<MgnregaMonthlyStatResponseDto> getStatsByDistrict(String districtName, Pageable pageable) {
		Page<MgnregaMonthlyStat> stats = statRepository.findAllByDistrict_DistrictNameIgnoreCase(districtName,
				pageable);
		return stats.map(this::mapToResponseDto);
	}

	// -------------------------
	// 4️⃣ Refresh all data (API ingestion)
	// -------------------------
	@Override
	@Transactional
	@CacheEvict(value = { CACHE_STATS_BY_STATE, CACHE_STATS_BY_DISTRICT }, allEntries = true)
	public void refreshAllData() {

		// ✅ Build the API URL dynamically
		String baseUrl = "https://api.data.gov.in";
		String apiUrl = String.format("%s/resource/%s?api-key=%s&format=%s&limit=%d", baseUrl, resourceId, apiKey,
				format, limit);

		// ✅ Start ingestion log using constants
		ApiIngestionLog logEntry = ingestionService.startIngestion(SOURCE_MGNREGA, apiUrl, FETCH_SCHEDULED_REFRESH);

		int totalFetched = 0;
		int totalFailed = 0;

		try {
			int offset = 0;
			boolean hasMore = true;

			while (hasMore) {
				// 🔄 Pagination URL
				String pagedUrl = apiUrl + "&offset=" + offset;

				Map response = webClient.get().uri(pagedUrl).retrieve().bodyToMono(Map.class).block();

				if (response == null || !response.containsKey("records"))
					break;

				List<Map<String, Object>> records = (List<Map<String, Object>>) response.get("records");
				if (records.isEmpty())
					break;

				for (Map<String, Object> record : records) {
					try {
						handleSingleRecord(record);
						totalFetched++;
					} catch (Exception e) {
						log.error("❌ Failed to save record: {}", e.getMessage());
						totalFailed++;
					}
				}

				offset += records.size();
				hasMore = records.size() == limit;
			}

			// ✅ Complete ingestion successfully
			ingestionService.completeIngestion(logEntry, STATUS_SUCCESS, totalFetched, totalFailed,
					"Ingestion completed successfully at " + Instant.now());

			log.info("✅ Data ingestion SUCCESS for [{}]: {} records fetched, {} failed", SOURCE_MGNREGA, totalFetched,
					totalFailed);

		} catch (Exception e) {
			// ✅ Complete ingestion with failure status
			ingestionService.completeIngestion(logEntry, STATUS_FAILURE, totalFetched, totalFailed,
					"Error occurred: " + e.getMessage());

			log.error("🔥 Data ingestion FAILURE for [{}]: {} fetched, {} failed — Error: {}", SOURCE_MGNREGA,
					totalFetched, totalFailed, e.getMessage());
		}
	}

	// -------------------------
	// 5️⃣ Count records (dashboard stat)
	// -------------------------
	@Override
	@Transactional(readOnly = true)
	public long countAllRecords() {
		return statRepository.count();
	}

	// -------------------------
	// 🔧 Helper Methods
	// -------------------------
	private void handleSingleRecord(Map<String, Object> record) {
		String stateName = (String) record.get("state_name");
		String stateCode = String.valueOf(record.get("state_code"));
		String districtName = (String) record.get("district_name");
		String districtCode = String.valueOf(record.get("district_code"));

		// Create or get State
		State state = stateRepository.findByStateCode(stateCode).orElseGet(
				() -> stateRepository.save(State.builder().stateCode(stateCode).stateName(stateName).build()));

		// Create or get District
		District district = districtRepository.findByDistrictCode(districtCode).orElseGet(() -> districtRepository
				.save(District.builder().districtCode(districtCode).districtName(districtName).state(state).build()));

		// Map record → DTO → Entity
		MgnregaMonthlyStatRequestDto dto = MgnregaMonthlyStatRequestDto.builder().districtCode(districtCode)
				.monthNo(mapMonthToNumber((String) record.get("month"))).financialYear((String) record.get("fin_year"))
				.householdsDemanded(parseLong(record.get("hh_dem")))
				.householdsProvided(parseLong(record.get("hh_prov")))
				.personsDemanded(parseLong(record.get("person_dem")))
				.personsProvided(parseLong(record.get("person_prov")))
				.persondaysGenerated(parseLong(record.get("persondays_gen")))
				.avgWageRate(parseBigDecimal(record.get("avg_wage_rate")))
				.totalExpenditure(parseBigDecimal(record.get("total_exp")))
				.worksCompleted(parseLong(record.get("works_completed")))
				.scShare(parseBigDecimal(record.get("sc_share"))).stShare(parseBigDecimal(record.get("st_share")))
				.womenShare(parseBigDecimal(record.get("women_share"))).build();

		saveOrUpdateMonthlyStat(dto);
	}

	private MgnregaMonthlyStatResponseDto mapToResponseDto(MgnregaMonthlyStat entity) {
		return MgnregaMonthlyStatResponseDto.builder().stateName(entity.getDistrict().getState().getStateName())
				.stateCode(entity.getDistrict().getState().getStateCode())
				.districtName(entity.getDistrict().getDistrictName())
				.districtCode(entity.getDistrict().getDistrictCode()).monthNo(entity.getMonthNo())
				.monthName(mapMonthName(entity.getMonthNo())).financialYear(entity.getFinancialYear())
				.householdsDemanded(entity.getHouseholdsDemanded()).householdsProvided(entity.getHouseholdsProvided())
				.personsDemanded(entity.getPersonsDemanded()).personsProvided(entity.getPersonsProvided())
				.persondaysGenerated(entity.getPersondaysGenerated()).avgWageRate(entity.getAvgWageRate())
				.totalExpenditure(entity.getTotalExpenditure()).worksCompleted(entity.getWorksCompleted())
				.scShare(entity.getScShare()).stShare(entity.getStShare()).womenShare(entity.getWomenShare()).build();
	}

	private int mapMonthToNumber(String monthName) {
		Map<String, Integer> map = Map.ofEntries(Map.entry("January", 1), Map.entry("February", 2),
				Map.entry("March", 3), Map.entry("April", 4), Map.entry("May", 5), Map.entry("June", 6),
				Map.entry("July", 7), Map.entry("August", 8), Map.entry("September", 9), Map.entry("October", 10),
				Map.entry("November", 11), Map.entry("December", 12));
		return map.getOrDefault(monthName, 0);
	}

	private String mapMonthName(int monthNo) {
		return List.of("January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
				"November", "December").get(Math.max(0, monthNo - 1));
	}

	private Long parseLong(Object obj) {
		try {
			return Long.parseLong(String.valueOf(obj));
		} catch (Exception e) {
			return 0L;
		}
	}

	private BigDecimal parseBigDecimal(Object obj) {
		try {
			return new BigDecimal(String.valueOf(obj));
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}
}
