package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("""
    SELECT j FROM Job j
    JOIN JobStat js ON j.jobId = js.job.jobId
    JOIN HandaliStat hs ON js.typeName = hs.stat.typeName
    WHERE hs.handali.handaliId = :handaliId
      AND hs.stat.value >= js.requiredStat
    GROUP BY j
    HAVING COUNT(js.jobStatId) = (
        SELECT COUNT(js_inner.jobStatId)
        FROM JobStat js_inner
        WHERE js_inner.job.jobId = j.jobId
    )
""")
    List<Job> findEligibleJobs(@Param("handaliId") Long handaliId);

}
