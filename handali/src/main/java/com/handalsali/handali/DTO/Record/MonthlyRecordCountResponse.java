package com.handalsali.handali.DTO.Record;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyRecordCountResponse {
    private int year;
    private int month;
    private long totalRecords;
}
