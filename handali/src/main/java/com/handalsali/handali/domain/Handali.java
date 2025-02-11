package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Handali {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="handali_id")
    private Long handaliId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false,name="start_date")
    private LocalDate startDate;

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
    @OneToOne(mappedBy = "handali", cascade = CascadeType.ALL, orphanRemoval = true)
    private Apart apart;

    @OneToMany(mappedBy = "handali", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HandaliStat> handaliStats;

    public Handali(String nickname,LocalDate startDate,User user){
        this.nickname=nickname;
        this.startDate=startDate;
        this.user=user;
    }
}
