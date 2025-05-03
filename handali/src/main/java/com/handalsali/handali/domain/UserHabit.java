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
    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (habit_id) REFERENCES habit(habit_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Habit habit;

    @Column
    private int month;

    public UserHabit(User user,Habit habit, int month){
        this.user=user;
        this.habit=habit;
        this.month=month;
    }

    public UserHabit(User user,Habit habit){
        this.user=user;
        this.habit=habit;
    }
}
