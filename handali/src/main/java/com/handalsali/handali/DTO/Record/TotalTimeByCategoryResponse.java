package com.handalsali.handali.DTO.Record;

import com.handalsali.handali.enums.Categoryname;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalTimeByCategoryResponse {
    private Categoryname category;
    private double total_time;
}
