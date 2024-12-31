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
    private long job_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int week_salary;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobStat> jobStats;

    public Job(String name, int week_salary){
        this.name=name;
        this.week_salary=week_salary;
    }
}
