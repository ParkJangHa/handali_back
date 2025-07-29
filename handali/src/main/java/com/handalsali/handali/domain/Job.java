package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Entity
@Getter
@EqualsAndHashCode(of = {"name", "weekSalary"})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return name.equals(job.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
