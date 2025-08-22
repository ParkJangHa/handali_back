package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name="handbook")
public class Handbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="handbook_id")
    private Long handbookId;

    @Column(unique = true, nullable = false)
    private String code;

    public Handbook(String code) {
        this.code = code;
    }
}
