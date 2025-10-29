package com.ab.repository;

import com.ab.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MgnregaMonthlyStatRepository extends JpaRepository<MgnregaMonthlyStat, UUID> {

    // ✅ Check if a record for same FinancialYear & month already exists
    boolean existsByFinancialYearAndMonthNo(FinancialYear financialYear, int monthNo);

    // ✅ Fetch the latest available month’s data for a given district
    @Query("""
        SELECT m
        FROM MgnregaMonthlyStat m
        JOIN m.financialYear fy
        JOIN fy.district d
        WHERE LOWER(d.districtName) = LOWER(:districtName)
        ORDER BY fy.financialYear DESC, m.monthNo DESC
    """)
    List<MgnregaMonthlyStat> findLatestByDistrictName(@Param("districtName") String districtName);

    // ✅ Filtered search for user (optional state/district/finYear)
    @Query("""
        SELECT m
        FROM MgnregaMonthlyStat m
        JOIN m.financialYear fy
        JOIN fy.district d
        JOIN d.state s
        WHERE (:stateName IS NULL OR LOWER(s.stateName) = LOWER(:stateName))
          AND (:districtName IS NULL OR LOWER(d.districtName) = LOWER(:districtName))
          AND (:finYear IS NULL OR fy.financialYear = :finYear)
        ORDER BY fy.financialYear DESC, m.monthNo DESC
    """)
    List<MgnregaMonthlyStat> findByFilters(
            @Param("stateName") String stateName,
            @Param("districtName") String districtName,
            @Param("finYear") String finYear
    );

    // ✅ Custom version of your old check:
    // now works with FinancialYear entity, not district directly
    @Query("""
        SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END
        FROM MgnregaMonthlyStat m
        WHERE m.financialYear = :financialYear
          AND m.monthNo = :monthNo
    """)
    boolean existsForMonthAndYear(
            @Param("financialYear") FinancialYear financialYear,
            @Param("monthNo") int monthNo
    );
}
