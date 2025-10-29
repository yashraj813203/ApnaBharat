package com.ab.repository;

import com.ab.entity.District;
import com.ab.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialYearRepository extends JpaRepository<FinancialYear, UUID> {

    Optional<FinancialYear> findByDistrictAndFinancialYear(District district, String financialYear);

    boolean existsByDistrictAndFinancialYear(District district, String financialYear);
}
