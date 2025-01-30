package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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
    @JoinColumn(name = "handali_id", nullable = false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (handali_id) REFERENCES handali(handali_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Handali handali;

    @ManyToOne
    @JoinColumn(name = "stat_id", nullable = false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (stat_id) REFERENCES stat(stat_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Stat stat;

    public HandaliStat(Handali handali, Stat stat) {
        this.handali=handali;
        this.stat=stat;
    }
}

