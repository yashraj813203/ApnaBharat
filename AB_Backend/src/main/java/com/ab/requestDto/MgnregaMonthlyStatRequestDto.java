package com.ab.requestDto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MgnregaMonthlyStatRequestDto {

	@NotNull(message = "District code cannot be null")
	private String districtCode;

	@NotNull(message = "Month number is required")
	@Min(value = 1, message = "Month number must be between 1 and 12")
	@Max(value = 12, message = "Month number must be between 1 and 12")
	private Integer monthNo;

	@NotBlank(message = "Financial year is required")
	private String financialYear; // e.g. "2024-2025"

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
