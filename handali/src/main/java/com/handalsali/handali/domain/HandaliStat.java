package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class HandaliStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long handali_stat_id;

    @ManyToOne
    @JoinColumn(name = "handali_id", nullable = false)
    private Handali handali;

    @ManyToOne
    @JoinColumn(name = "stat_id", nullable = false)
    private Stat stat;
}

