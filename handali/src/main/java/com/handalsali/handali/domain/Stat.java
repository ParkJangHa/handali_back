package com.handalsali.handali.domain;

import com.handalsali.handali.enums.TypeName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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
    @Setter
    private float value;

    @Column(nullable = false)
    @ColumnDefault("0")
    @Setter
    private float lastMonthValue;

    public Stat(TypeName typeName) {
        this.typeName=typeName;
    }
}
