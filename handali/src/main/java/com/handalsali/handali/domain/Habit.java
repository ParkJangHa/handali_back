package com.handalsali.handali.domain;

import com.handalsali.handali.Enums_MultyKey.Categoryname;
import com.handalsali.handali.Enums_MultyKey.CreatedType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Getter
@Table(name="habit")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long habit_id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Categoryname category_name;

    @Column(nullable = false)
    private String detailed_habit_name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CreatedType created_type;

    @Column(nullable = false)
    private LocalDate month;

    //이전에 저장하다. 데이터베이스에 저장하기 전에 현재 시간을 계산하는 로직을 추가
    @PrePersist
    protected void onCreate(){
        this.month=LocalDate.now();
    }

    public Habit(Categoryname category_name,String detailed_habit_name,CreatedType created_type){
        this.category_name=category_name;
        this.detailed_habit_name=detailed_habit_name;
        this.created_type=created_type;

//        //add
//        this.month=LocalDate.now();
    }
}
