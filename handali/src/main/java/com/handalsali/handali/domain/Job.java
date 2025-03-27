package com.handalsali.handali.domain;

import com.handalsali.handali.DTO.JobStatDTO;
import com.handalsali.handali.enums_multyKey.TypeName;
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

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Handali> handalis;

    @Column(nullable = false,name="week_salary")
    private int weekSalary;

    public Job(String name, int weekSalary){
        this.name=name;
        this.weekSalary=weekSalary;
    }
}
