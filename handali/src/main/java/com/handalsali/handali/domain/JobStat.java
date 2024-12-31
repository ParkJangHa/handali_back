package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class JobStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long job_stat_id;

    @Column(nullable = false)
    private float required_stat;

    @ManyToOne
    @JoinColumn(name="stat_id",nullable = false)
    private Stat stat;

    @ManyToOne
    @JoinColumn(name="job_id",nullable = false)
    private Job job;
}
