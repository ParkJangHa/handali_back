package com.handalsali.handali.domain;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.JobStatDTO;
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
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "apart_id", referencedColumnName = "apart_id"),
            @JoinColumn(name = "floor", referencedColumnName = "floor")
    })
    private Apart apart;

    public Handali(String nickname,LocalDate startDate,User user){
        this.nickname=nickname;
        this.startDate=startDate;
        this.user=user;
        this.job = job;
        this.apart = apart;
    }

    public HandaliDTO.HandaliInApartmentResponse toApartmentResponse() {
        return new HandaliDTO.HandaliInApartmentResponse(
                this.apart.getApartId().getApartId(), // 아파트 ID
                this.apart.getApartId().getFloor(), // 층 수
                this.nickname, // 닉네임
                this.startDate, // 생성일
                this.job != null ? this.job.getName() : null, // 직업명
                this.job != null ? this.job.getWeekSalary() : 0, // 주급
                "체력", // 예제 스탯 이름 (이 부분은 필요에 따라 변경)
                30.5f // 예제 스탯 값 (이 부분은 필요에 따라 변경)
        );
    }
}
