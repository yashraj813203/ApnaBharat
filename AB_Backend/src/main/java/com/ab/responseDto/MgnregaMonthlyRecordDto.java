package com.ab.responseDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MgnregaMonthlyRecordDto {

    @JsonProperty("fin_year")
    private String fin_year;

    @JsonProperty("month")
    private String month;

    @JsonProperty("state_code")
    private String state_code;

    @JsonProperty("state_name")
    private String state_name;

    @JsonProperty("district_code")
    private String district_code;

    @JsonProperty("district_name")
    private String district_name;

    @JsonProperty("Approved_Labour_Budget")
    private Long approved_Labour_Budget;

    @JsonProperty("Average_Wage_rate_per_day_per_person")
    private BigDecimal average_Wage_rate_per_day_per_person;

    @JsonProperty("Average_days_of_employment_provided_per_Household")
    private Long average_days_of_employment_provided_per_Household;

    @JsonProperty("Differently_abled_persons_worked")
    private Long differently_abled_persons_worked;

    @JsonProperty("Material_and_skilled_Wages")
    private BigDecimal material_and_skilled_Wages;

    @JsonProperty("Number_of_Completed_Works")
    private Long number_of_Completed_Works;

    @JsonProperty("Number_of_GPs_with_NIL_exp")
    private Long number_of_GPs_with_NIL_exp;

    @JsonProperty("Number_of_Ongoing_Works")
    private Long number_of_Ongoing_Works;

    @JsonProperty("Persondays_of_Central_Liability_so_far")
    private Long persondays_of_Central_Liability_so_far;

    @JsonProperty("SC_persondays")
    private Long sc_persondays;

    @JsonProperty("SC_workers_against_active_workers")
    private Long sc_workers_against_active_workers;

    @JsonProperty("ST_persondays")
    private Long st_persondays;

    @JsonProperty("ST_workers_against_active_workers")
    private Long st_workers_against_active_workers;

    @JsonProperty("Total_Adm_Expenditure")
    private BigDecimal total_Adm_Expenditure;

    @JsonProperty("Total_Exp")
    private BigDecimal total_Exp;

    @JsonProperty("Total_Households_Worked")
    private Long total_Households_Worked;

    @JsonProperty("Total_Individuals_Worked")
    private Long total_Individuals_Worked;

    @JsonProperty("Total_No_of_Active_Job_Cards")
    private Long total_No_of_Active_Job_Cards;

    @JsonProperty("Total_No_of_Active_Workers")
    private Long total_No_of_Active_Workers;

    @JsonProperty("Total_No_of_HHs_completed_100_Days_of_Wage_Employment")
    private Long total_No_of_HHs_completed_100_Days_of_Wage_Employment;

    @JsonProperty("Total_No_of_JobCards_issued")
    private Long total_No_of_JobCards_issued;

    @JsonProperty("Total_No_of_Workers")
    private Long total_No_of_Workers;

    @JsonProperty("Total_No_of_Works_Takenup")
    private Long total_No_of_Works_Takenup;

    @JsonProperty("Wages")
    private BigDecimal wages;

    @JsonProperty("Women_Persondays")
    private Long women_Persondays;

    @JsonProperty("percent_of_Category_B_Works")
    private BigDecimal percent_of_Category_B_Works;

    @JsonProperty("percent_of_Expenditure_on_Agriculture_Allied_Works")
    private BigDecimal percent_of_Expenditure_on_Agriculture_Allied_Works;

    @JsonProperty("percent_of_NRM_Expenditure")
    private BigDecimal percent_of_NRM_Expenditure;

    @JsonProperty("percentage_payments_gererated_within_15_days")
    private BigDecimal percentage_payments_gererated_within_15_days;

    @JsonProperty("Remarks")
    private String remarks;
}
