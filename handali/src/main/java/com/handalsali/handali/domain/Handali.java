package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Handali {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long handali_id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Date startDate;

    @ManyToOne
    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private User user;

    @ManyToOne
    @JoinColumn(name="job_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (job_id) REFERENCES job(job_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Job job;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "apart_id", referencedColumnName = "apart_id"),
            @JoinColumn(name = "floor", referencedColumnName = "floor")
    })
    private Apart apart;
}
