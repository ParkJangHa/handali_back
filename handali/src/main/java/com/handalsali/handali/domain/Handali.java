package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Handali {

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="handali_id")
    private long handaliId;

    @Setter
    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false,name="start_date")
    @Setter
    private LocalDate startDate;

    @Setter
    @ManyToOne
    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name="job_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (job_id) REFERENCES job(job_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Job job;

    @Setter
    @OneToOne
    @JoinColumn(name="apart_total_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (apart_total_id) REFERENCES apart(apart_total_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Apart apart;

    @Setter
    private String image="image_0_0_0.png";

    public Handali(String nickname,LocalDate startDate,User user){
        this.nickname=nickname;
        this.startDate=startDate;
        this.user=user;
    }
}
