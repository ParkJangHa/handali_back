package com.handalsali.handali.domain;

import com.handalsali.handali.Enums_MultyKey.TypeName;
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
    private long stat_id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeName type_name;

    @Column(nullable = false)
    @ColumnDefault("0")
    private float value;

    @OneToMany(mappedBy = "stat", cascade = CascadeType.ALL)
    private List<JobStat> jobStats; // Job-Stat 관계

    public Stat(TypeName type_name, float value){
        this.type_name=type_name;
        this.value=value;
    }
}
