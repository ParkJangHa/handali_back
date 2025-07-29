package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.enums.TypeName;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JobServiceTest {
    //목표:
    // 1. HandaliStat이 비어 있음 -> "백수" 직업 반환
    // 2. HandaliStat이 있고, 적절한 Job이 없음 -> "백수" 직업 반환 (?)
    // 3. HandaliStat이 있고, 적절한 Job이 있음 -> 가중치 기반 직업 하나 반환

    @Mock private JobRepository jobRepository;
    @Mock private HandaliStatService handaliStatService;
    @InjectMocks private JobService jobService;

    private Handali handali;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handali = new Handali();
        handali.setHandaliId(1L);
    }

    // 1. HandaliStat이 비어 있음 -> "백수" 직업 반환
    @Test
    void assignBestJobToHandali_WhenNoStats_ReturnsUnemployedJob() {
        // Given
        when(handaliStatService.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(List.of());

        Job unemployed = new Job("백수", 0);
        when(jobRepository.findByName("백수")).thenReturn(unemployed);
        when(jobRepository.save(unemployed)).thenReturn(unemployed);

        // When
        Job result = jobService.assignBestJobToHandali(handali);

        // Then
        assertEquals("백수", result.getName());
    }

    // 2. HandaliStat이 있고, 적절한 Job이 없음 -> "백수" 직업 반환 (?)
    @Test
    void assignBestJobToHandali_WhenNoMatchingJobs_ReturnsUnemployedJob() {
        // Given
        Stat stat = new Stat(TypeName.INTELLIGENT_SKILL);  // 임의의 stat
        HandaliStat maxStat = new HandaliStat(handali, stat);
        maxStat.setValue(70); // 스탯 수치 지정

        when(handaliStatService.findMaxStatByHandaliId(handali.getHandaliId()))
                .thenReturn(List.of(maxStat));

        when(jobRepository.findJobByMaxHandaliStat(TypeName.INTELLIGENT_SKILL, 70)).thenReturn(List.of());

        Job unemployed = new Job("백수", 0);
        when(jobRepository.findByName("백수")).thenReturn(unemployed);
        when(jobRepository.save(unemployed)).thenReturn(unemployed);

        // When
        Job result = jobService.assignBestJobToHandali(handali);

        // Then
        assertEquals("백수", result.getName());
    }


    // 3. HandaliStat이 있고, 적절한 Job이 있음 -> 가중치 기반 직업 하나 반환
    @Test
    void assignBestJobToHandali_WhenMatchingJobsExist_ReturnsWeightedRandomJob() {
        // Given
        Stat stat = new Stat(TypeName.INTELLIGENT_SKILL);
        // (float 값이지만 HandaliStat에는 int로 변환해서 저장)
        float statValue = 90f;

        HandaliStat maxStat = new HandaliStat(handali, stat);
        maxStat.setValue((int)statValue);

        when(handaliStatService.findMaxStatByHandaliId(handali.getHandaliId()))
                .thenReturn(List.of(maxStat));

        // 가중치 기반 랜덤 선택될 Job 리스트 준비
        Job jobA = new Job("개발자", 800);
        Job jobB = new Job("디자이너", 600);
        Job jobC = new Job("마케터", 400);
        List<Job> jobs = List.of(jobA, jobB, jobC);

        // 조건에 맞는 직업 리스트 반환
        when(jobRepository.findJobByMaxHandaliStat(TypeName.INTELLIGENT_SKILL, statValue))
                .thenReturn(jobs);

        // When
        Job result = jobService.assignBestJobToHandali(handali);

        // Then
        //반환된 직업은 null이 아니어야 하고, 준비한 직업들 중 하나여야 한다.
        assertNotNull(result, "JobService가 null을 반환했습니다.");
        assertTrue(
                jobs.stream().anyMatch(job -> job.getName().equals(result.getName())),
                "반환된 직업은 예상 직업 리스트에 없습니다."
        );
    }
}
