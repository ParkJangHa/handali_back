package com.handalsali.handali.DTO.Record;

import com.handalsali.handali.enums.Categoryname;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SatisfactionAvgByCategoryResponse {
    private Categoryname category;
    private Double avg_satisfaction;
}
