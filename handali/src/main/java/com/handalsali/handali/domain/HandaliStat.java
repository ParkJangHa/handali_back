package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name="handali_stat")
public class HandaliStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="handali_stat_id")
    private Long HandaliStatId;

    @ManyToOne
    @JoinColumn(name = "handali_id", nullable = false)
    private Handali handali;

    @ManyToOne
    @JoinColumn(name = "stat_id", nullable = false)
    private Stat stat;

    public HandaliStat(Handali handali, Stat stat){
        this.handali=handali;
        this.stat=stat;
    }
}

