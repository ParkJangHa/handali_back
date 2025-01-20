package com.handalsali.handali.domain;

import com.handalsali.handali.enums_multyKey.TypeName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@NoArgsConstructor
@Entity
@Getter
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="stat_id")
    private long statId;

    @Column(nullable = false,name="type_name")
    @Enumerated(EnumType.STRING)
    private TypeName typeName;

    @Column(name = "value", nullable = false)
    @ColumnDefault("0")
    private double value;

    @OneToMany(mappedBy = "stat", cascade = CascadeType.ALL)
    private List<JobStat> jobStats; // Job-Stat 관계

    @ManyToOne
    @JoinColumn(name = "handali_id", foreignKey = @ForeignKey(name = "fk_stat_handali"))
    private Handali handali;
}
