package com.handalsali.handali.DTO;

import com.handalsali.handali.domain.Job;
import com.handalsali.handali.enums.TypeName;
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

    public static JobResponse fromEntity(Job job, TypeName typeName, float value) {
        return new JobResponse(
                job.getWeekSalary(),
                job.getName(),
                new JobStat(typeName, value)
        );
    }
}
