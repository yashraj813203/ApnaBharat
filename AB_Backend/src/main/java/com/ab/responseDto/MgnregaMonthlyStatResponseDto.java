package com.ab.responseDto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class MgnregaMonthlyStatResponseDto {

    private String stateName;
    private String stateCode;
    private String districtName;
    private String districtCode;
    private String financialYear;
    private String monthName;

    private Long approvedLabourBudget;
    private BigDecimal averageWageRate;
    private Long averageDaysOfEmployment;
    private Long differentlyAbledPersonsWorked;
    private BigDecimal materialAndSkilledWages;
    private Long numberOfCompletedWorks;
    private Long numberOfGPsWithNilExp;
    private Long numberOfOngoingWorks;
    private Long persondaysCentralLiability;
    private Long scPersondays;
    private Long scWorkersAgainstActiveWorkers;
    private Long stPersondays;
    private Long stWorkersAgainstActiveWorkers;
    private BigDecimal totalAdminExpenditure;
    private BigDecimal totalExpenditure;
    private Long totalHouseholdsWorked;
    private Long totalIndividualsWorked;
    private Long totalActiveJobcards;
    private Long totalActiveWorkers;
    private Long totalHHsCompleted100Days;
    private Long totalJobcardsIssued;
    private Long totalWorkers;
    private Long totalWorksTakenUp;
    private BigDecimal wages;
    private Long womenPersondays;
    private BigDecimal percentCategoryBWorks;
    private BigDecimal percentExpAgriAllied;
    private BigDecimal percentNrmExpenditure;
    private BigDecimal percentPaymentsWithin15Days;
    private String remarks;
}
