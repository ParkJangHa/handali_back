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
    @Column(name="record_id")
    private long recordId;

//    @ManyToOne
//    @JoinColumn(name="user_id",
//            foreignKey = @ForeignKey (
//            name="fk_record_user"))
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
    private Date date;
}
