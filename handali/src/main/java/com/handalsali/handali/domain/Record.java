package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name="record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long record_id;

    @ManyToOne
    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey (foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private User user;

    @ManyToOne
    @JoinColumn(name="habit_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (habit_id) REFERENCES habit(habit_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Habit habit;

    @Column(nullable = false)
    private float time;

    @Column(nullable = false)
    private int satisfaction;

    @Column(nullable = false)
    private Date date;
}
