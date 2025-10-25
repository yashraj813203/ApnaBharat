package com.ab.responseDto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MgnregaMonthlyStatResponseDto {

	private String stateName;
	private String stateCode;
	private String districtName;
	private String districtCode;

	private Integer monthNo;
	private String monthName; // for display convenience ("April", "May", etc.)
	private String financialYear;

	private Long householdsDemanded;
	private Long householdsProvided;
	private Long personsDemanded;
	private Long personsProvided;
	private Long persondaysGenerated;

	private BigDecimal avgWageRate;
	private BigDecimal totalExpenditure;
	private Long worksCompleted;
	private BigDecimal scShare;
	private BigDecimal stShare;
	private BigDecimal womenShare;
}
