package com.ab.responseDto;

import lombok.Data;
import java.util.List;

@Data
public class MgnregaApiResponseDto {
    private String index_name;
    private String title;
    private String desc;
    private String org_type;
    private String source;
    private String status;
    private String message;
    private int total;
    private int count;
    private String limit;
    private String offset;

    private List<MgnregaMonthlyRecordDto> records; // Each record = 1 district-month entry
}
