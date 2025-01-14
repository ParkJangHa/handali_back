package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="user_habit")
public class UserHabit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_habit_id")
    private long userHabitId;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="habit_id",nullable = false)
    private Habit habit;

    @Column
    private int month;

    public UserHabit(User user,Habit habit, int month){
        this.user=user;
        this.habit=habit;
        this.month=month;
    }
}
