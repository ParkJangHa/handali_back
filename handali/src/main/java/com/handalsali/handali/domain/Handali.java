package com.handalsali.handali.domain;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.JobStatDTO;
import com.handalsali.handali.enums_multyKey.ApartId;
import com.handalsali.handali.enums_multyKey.TypeName;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="handali_id")
    private long handaliId;

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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "apart_id", referencedColumnName = "apart_id"),
            @JoinColumn(name = "floor", referencedColumnName = "floor")
    })
    private Apart apart;

    public Handali(String nickname,LocalDate startDate,User user){
        this.nickname=nickname;
        this.startDate=startDate;
        this.user=user;
    }

    public void setFloor(int floor) {
        this.apart = new Apart(new ApartId(this.apart.getApartId().getApartId(), floor), this.apart.getUser());
    }
}
