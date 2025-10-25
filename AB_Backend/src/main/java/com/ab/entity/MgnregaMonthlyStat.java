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
@Table(name = "mgnrega_monthly_stats", uniqueConstraints = {
		@UniqueConstraint(name = "uk_stats_district_month_fy", columnNames = { "district_id", "month_no",
				"financial_year" }) }, indexes = { @Index(name = "idx_stats_district", columnList = "district_id"),
						@Index(name = "idx_stats_month", columnList = "month_no"),
						@Index(name = "idx_stats_finyear", columnList = "financial_year") })
public class MgnregaMonthlyStat extends BaseEntity {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "district_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stats_district"))
	private District district;

	// -------------------- CORE KEYS --------------------
	@Column(name = "month_no", nullable = false)
	private int monthNo; // 1-12 based on month name

	@Column(name = "financial_year", nullable = false, length = 9)
	private String financialYear; // e.g., "2024-2025"

	// -------------------- PERFORMANCE DATA --------------------
	@Column(name = "households_demanded")
	private Long householdsDemanded; // hh_dem

	@Column(name = "households_provided")
	private Long householdsProvided; // hh_prov

	@Column(name = "persons_demanded")
	private Long personsDemanded; // person_dem

	@Column(name = "persons_provided")
	private Long personsProvided; // person_prov

	@Column(name = "persondays_generated")
	private Long persondaysGenerated; // persondays_gen

	@Column(name = "avg_wage_rate", precision = 10, scale = 2)
	private BigDecimal avgWageRate; // avg_wage_rate

	@Column(name = "total_expenditure", precision = 18, scale = 2)
	private BigDecimal totalExpenditure; // total_exp

	@Column(name = "works_completed")
	private Long worksCompleted; // works_completed

	// -------------------- PARTICIPATION METRICS --------------------
	@Column(name = "sc_share", precision = 5, scale = 2)
	private BigDecimal scShare; // % of SC participation

	@Column(name = "st_share", precision = 5, scale = 2)
	private BigDecimal stShare; // % of ST participation

	@Column(name = "women_share", precision = 5, scale = 2)
	private BigDecimal womenShare; // % of Women participation
}
