package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Entity
@Getter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="job_id")
    private long jobId;

    @Setter
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Handali> handalis;

    @Setter
    @Column(nullable = false,name="week_salary")
    private int weekSalary;

    public Job(String name, int weekSalary){
        this.name=name;
        this.weekSalary=weekSalary;
    }
}
