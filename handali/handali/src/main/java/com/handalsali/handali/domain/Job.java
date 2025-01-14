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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,name="week_salary")
    private int weekSalary;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobStat> jobStats;

    public Job(String name, int weekSalary){
        this.name=name;
        this.weekSalary=weekSalary;
    }
}
