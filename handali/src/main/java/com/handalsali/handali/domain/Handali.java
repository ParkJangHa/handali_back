package com.handalsali.handali.domain;

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

    // Job 정보 포함한 DTO 변환 메서드 추가
    public JobStatDTO.JobResponse toJobResponse() {
        return new JobStatDTO.JobResponse(
                this.job.getWeekSalary(),  // Job의 주급
                this.job.getName(),        // Job의 이름
                new JobStatDTO.JobStat(TypeName.valueOf("체력"), 50.0f) // 기본값 설정 (필요 시 변경)
        );
    }
}
