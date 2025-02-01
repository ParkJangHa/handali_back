package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Entity
@Getter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="job_id")
    private long jobId;

    //명확한게 좋을거 같아서 jobName으로 수정함 - 02.01
    @Column(nullable = false)
    private String jobName;

    @Column(nullable = false,name="week_salary")
    private int weekSalary;

    public Job(String jobName, int weekSalary){
        this.jobName=jobName;
        this.weekSalary=weekSalary;
    }
}
