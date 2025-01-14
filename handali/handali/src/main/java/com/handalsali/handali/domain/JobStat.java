package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@Table(name="job_stat")
public class JobStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="job_stat_id")
    private long jobStatId;

    @Column(nullable = false,name="required_stat")
    private float requiredStat;

    @ManyToOne
    @JoinColumn(name="stat_id",nullable = false)
    private Stat stat;

    @ManyToOne
    @JoinColumn(name="job_id",nullable = false)
    private Job job;
}
