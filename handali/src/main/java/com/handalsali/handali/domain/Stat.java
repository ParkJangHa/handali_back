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

    @Column(nullable = false)
    @ColumnDefault("0")
    private float value;

    @OneToMany(mappedBy = "stat", cascade = CascadeType.ALL)
    private List<JobStat> jobStats; // Job-Stat 관계

    public Stat(TypeName typeName, float value){
        this.typeName=typeName;
        this.value=value;
    }
}