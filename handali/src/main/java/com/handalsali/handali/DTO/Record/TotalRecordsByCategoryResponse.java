package com.handalsali.handali.DTO.Record;

import com.handalsali.handali.enums.Categoryname;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalRecordsByCategoryResponse {
    private Categoryname category;
    private long total_records;
}
