package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name="record",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"habit_id", "date"})})
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="record_id")
    private long recordId;

    @ManyToOne
    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey (foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE",
                    name="fk_record_user"))
    private User user;

    @ManyToOne
    @JoinColumn(name="habit_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (habit_id) REFERENCES habit(habit_id) ON DELETE CASCADE ON UPDATE CASCADE",
            name="fk_record_habit"))
    private Habit habit;

    @Column(nullable = false)
    private float time;

    @Column(nullable = false)
    private int satisfaction;

    @Column(nullable = false)
    private LocalDate date;

    public Record(User user, Habit habit, float time, int satisfaction, LocalDate date){
        this.user=user;
        this.habit=habit;
        this.time=time;
        this.satisfaction=satisfaction;
        this.date=date;
    }
}
