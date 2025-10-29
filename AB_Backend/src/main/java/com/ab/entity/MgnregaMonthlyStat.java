package com.ab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "mgnrega_monthly_stats",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_fy_month",
        columnNames = {"finyear_id", "month_no"}
    ),
    indexes = {
        @Index(name = "idx_finyear_id", columnList = "finyear_id"),
        @Index(name = "idx_month_no", columnList = "month_no")
    }
)
public class MgnregaMonthlyStat extends BaseEntity {

    // 🔗 Relation to FinancialYear
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "finyear_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_stat_finyear")
    )
    private FinancialYear financialYear;

    @Column(name = "month_no", nullable = false)
    private int monthNo;

    @Column(name = "approved_labour_budget")
    private Long approvedLabourBudget;

    @Column(name = "average_wage_rate", precision = 12, scale = 2)
    private BigDecimal averageWageRate;

    @Column(name = "average_days_of_employment")
    private Long averageDaysOfEmployment;

    @Column(name = "differently_abled_persons_worked")
    private Long differentlyAbledPersonsWorked;

    @Column(name = "material_and_skilled_wages", precision = 18, scale = 2)
    private BigDecimal materialAndSkilledWages;

    @Column(name = "number_of_completed_works")
    private Long numberOfCompletedWorks;

    @Column(name = "number_of_gps_with_nil_exp")
    private Long numberOfGPsWithNilExp;

    @Column(name = "number_of_ongoing_works")
    private Long numberOfOngoingWorks;

    @Column(name = "persondays_central_liability")
    private Long persondaysCentralLiability;

    @Column(name = "sc_persondays")
    private Long scPersondays;

    @Column(name = "sc_workers_against_active_workers")
    private Long scWorkersAgainstActiveWorkers;

    @Column(name = "st_persondays")
    private Long stPersondays;

    @Column(name = "st_workers_against_active_workers")
    private Long stWorkersAgainstActiveWorkers;

    @Column(name = "total_admin_expenditure", precision = 18, scale = 2)
    private BigDecimal totalAdminExpenditure;

    @Column(name = "total_expenditure", precision = 18, scale = 2)
    private BigDecimal totalExpenditure;

    @Column(name = "total_households_worked")
    private Long totalHouseholdsWorked;

    @Column(name = "total_individuals_worked")
    private Long totalIndividualsWorked;

    @Column(name = "total_active_jobcards")
    private Long totalActiveJobcards;

    @Column(name = "total_active_workers")
    private Long totalActiveWorkers;

    @Column(name = "total_hhs_completed_100_days")
    private Long totalHHsCompleted100Days;

    @Column(name = "total_jobcards_issued")
    private Long totalJobcardsIssued;

    @Column(name = "total_workers")
    private Long totalWorkers;

    @Column(name = "total_works_taken_up")
    private Long totalWorksTakenUp;

    @Column(name = "wages", precision = 18, scale = 2)
    private BigDecimal wages;

    @Column(name = "women_persondays")
    private Long womenPersondays;

    @Column(name = "percent_category_b_works", precision = 10, scale = 2)
    private BigDecimal percentCategoryBWorks;

    @Column(name = "percent_exp_agri_allied", precision = 10, scale = 2)
    private BigDecimal percentExpAgriAllied;

    @Column(name = "percent_nrm_expenditure", precision = 10, scale = 2)
    private BigDecimal percentNrmExpenditure;

    @Column(name = "percent_payments_within_15_days", precision = 10, scale = 2)
    private BigDecimal percentPaymentsWithin15Days;

    @Column(name = "remarks", length = 255)
    private String remarks;

    // ----------------- BACKWARD COMPATIBILITY HELPERS -----------------
    @Transient
    public District getDistrict() {
        return financialYear != null ? financialYear.getDistrict() : null;
    }

    @Transient
    public State getState() {
        return financialYear != null && financialYear.getDistrict() != null
                ? financialYear.getDistrict().getState()
                : null;
    }

    @Transient
    public String getFinancialYearString() {
        return financialYear != null ? financialYear.getFinancialYear() : null;
    }
}
