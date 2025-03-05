package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.TypeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    /**한달이의 최대 스탯명과 동일하고, 스탯값보다 큰 직업 찾기*/
    @Query("select j from Job j " +
            "join JobStat js on js.job=j " +
            "where js.typeName=:typeName and " +
            "js.requiredStat<=:requiredStat")
    List<Job> findJobByMaxHandaliStat(@Param("typeName") TypeName typeName,@Param("requiredStat")float requiredStat);

    //직업명으로 직업 찾기
    Job findByName(String name);

    /** 최신 한달이 찾기**/
    @Query("SELECT h FROM Handali h WHERE h.user = :user ORDER BY h.startDate DESC LIMIT 1")
    Handali findLatestHandaliByUser(@Param("user") User user);
}
