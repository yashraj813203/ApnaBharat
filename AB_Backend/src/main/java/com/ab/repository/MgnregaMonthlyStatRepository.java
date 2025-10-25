package com.ab.repository;

import com.ab.entity.MgnregaMonthlyStat;
import com.ab.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MgnregaMonthlyStatRepository extends JpaRepository<MgnregaMonthlyStat, UUID> {

	Optional<MgnregaMonthlyStat> findByDistrictAndMonthNoAndFinancialYear(District district, int monthNo,
			String financialYear);

	Page<MgnregaMonthlyStat> findAllByDistrict_State_StateNameIgnoreCase(String stateName, Pageable pageable);

	Page<MgnregaMonthlyStat> findAllByDistrict_DistrictNameIgnoreCase(String districtName, Pageable pageable);
}
