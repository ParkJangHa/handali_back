package com.handalsali.handali.domain;

import com.handalsali.handali.enums_multyKey.TypeName;
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

    @Column(nullable = false,name="type_name")
    @Enumerated(EnumType.STRING)
    private TypeName typeName;

//    @ManyToOne
//    @JoinColumn(name="stat_id",nullable = false)
//    private Stat stat;

    @ManyToOne
    @JoinColumn(name="job_id",nullable = false)
    private Job job;
}
