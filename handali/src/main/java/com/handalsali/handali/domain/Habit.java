package com.handalsali.handali.domain;

import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
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
    @Column(name="habit_id")
    private long habitId;

    @Column(nullable = false,name="category_name")
    @Enumerated(EnumType.STRING)
    private Categoryname categoryName;

    @Column(nullable = false,name="detailed_habit_name")
    private String detailedHabitName;

    @Column(nullable = false,name="created_type")
    @Enumerated(EnumType.STRING)
    private CreatedType createdType;

    @Column(nullable = false)
    private LocalDate month;


    public Habit(Categoryname categoryName,String detailedHabit_name,CreatedType createdType, LocalDate month){
        this.categoryName=categoryName;
        this.detailedHabitName=detailedHabitName;
        this.createdType=createdType;
        this.month=month;
    }
}
