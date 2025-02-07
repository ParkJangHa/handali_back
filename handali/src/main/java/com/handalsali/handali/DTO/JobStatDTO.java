package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

public class JobStatDTO {
    @Data
    @AllArgsConstructor
    public static class JobResponse{
        private int salary;
        private String name; //job_name -> name으로 변경 02/06
        private JobStat stat;
    }

    @Data
    @AllArgsConstructor
    public static class JobStat{
        private TypeName type_name;
        private float value;
    }
}
