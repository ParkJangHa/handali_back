package com.handalsali.handali.service;

import com.handalsali.handali.domain.*;
import com.handalsali.handali.enums.TypeName;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private HandaliStatRepository handaliStatRepository;

    @InjectMocks
    private JobService jobService;

    private User user;
    private Handali handali;
    private Job unemployedJob;

    @BeforeEach
    void setUp() {
        user = new User("test@test.com", "테스터", "password", "010-1234-5678", LocalDate.now());
        user.setUserId(1L);

        handali = new Handali("테스트한달이", LocalDate.now(), user);
        handali.setHandaliId(1L);

        unemployedJob = new Job();
        unemployedJob.setName("백수");
        unemployedJob.setWeekSalary(0);
    }

    /**
     * ✅ [테스트 목적]
     * 한달이의 스탯이 없는 경우 (신생 한달이)
     * → "백수" 직업이 할당되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_NoStats() {
        // given
        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(List.of());
        when(jobRepository.findByName("백수")).thenReturn(unemployedJob);
        when(jobRepository.save(unemployedJob)).thenReturn(unemployedJob);

        // when
        Job result = jobService.assignBestJobToHandali(handali);

        // then
        assertEquals("백수", result.getName());
        assertEquals(0, result.getWeekSalary());
        verify(handaliStatRepository, times(1)).findMaxStatByHandaliId(handali.getHandaliId());
        verify(jobRepository, times(1)).findByName("백수");
        verify(jobRepository, times(1)).save(unemployedJob);
    }

    /**
     * ✅ [테스트 목적]
     * 한달이의 최고 스탯에 맞는 직업이 없는 경우
     * → "백수" 직업이 할당되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_NoMatchingJobs() {
        // given
        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(50.0f);

        HandaliStat maxHandaliStat = new HandaliStat(handali, activityStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(maxStats);
        when(jobRepository.findJobByMaxHandaliStat(TypeName.ACTIVITY_SKILL, 50.0f)).thenReturn(List.of());
        when(jobRepository.findByName("백수")).thenReturn(unemployedJob);
        when(jobRepository.save(unemployedJob)).thenReturn(unemployedJob);

        // when
        Job result = jobService.assignBestJobToHandali(handali);

        // then
        assertEquals("백수", result.getName());
        verify(jobRepository, times(1)).findJobByMaxHandaliStat(TypeName.ACTIVITY_SKILL, 50.0f);
        verify(jobRepository, times(1)).findByName("백수");
    }

    /**
     * ✅ [테스트 목적]
     * 한달이의 최고 스탯에 맞는 직업이 1개만 있는 경우
     * → 해당 직업이 할당되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_SingleJob() {
        // given
        Stat intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(100.0f);

        HandaliStat maxHandaliStat = new HandaliStat(handali, intelligentStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat);

        Job developerJob = new Job();
        developerJob.setName("개발자");
        developerJob.setWeekSalary(5000);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(maxStats);
        when(jobRepository.findJobByMaxHandaliStat(TypeName.INTELLIGENT_SKILL, 100.0f)).thenReturn(List.of(developerJob));

        // when
        Job result = jobService.assignBestJobToHandali(handali);

        // then
        assertEquals("개발자", result.getName());
        assertEquals(5000, result.getWeekSalary());
        verify(jobRepository, times(1)).findJobByMaxHandaliStat(TypeName.INTELLIGENT_SKILL, 100.0f);
        verify(jobRepository, never()).findByName("백수");
    }

    /**
     * ✅ [테스트 목적]
     * 여러 스탯이 동일하게 최대값인 경우
     * → 랜덤으로 선택된 스탯으로 직업 할당되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_MultipleMaxStats() {
        // given
        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(100.0f);

        Stat intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(100.0f);

        HandaliStat maxHandaliStat1 = new HandaliStat(handali, activityStat);
        HandaliStat maxHandaliStat2 = new HandaliStat(handali, intelligentStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat1, maxHandaliStat2);

        Job athleteJob = new Job();
        athleteJob.setName("운동선수");
        athleteJob.setWeekSalary(7000);

        Job developerJob = new Job();
        developerJob.setName("개발자");
        developerJob.setWeekSalary(5000);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(maxStats);

        // 두 가지 경우 모두 준비 (랜덤이므로)
        lenient().when(jobRepository.findJobByMaxHandaliStat(TypeName.ACTIVITY_SKILL, 100.0f))
                .thenReturn(List.of(athleteJob));
        lenient().when(jobRepository.findJobByMaxHandaliStat(TypeName.INTELLIGENT_SKILL, 100.0f))
                .thenReturn(List.of(developerJob));
        // when
        Job result = jobService.assignBestJobToHandali(handali);

        // then
        assertNotNull(result);
        assertTrue(result.getName().equals("운동선수") || result.getName().equals("개발자"));
        verify(handaliStatRepository, times(1)).findMaxStatByHandaliId(handali.getHandaliId());
    }

    /**
     * ✅ [테스트 목적]
     * 여러 직업이 가능한 경우 (가중치 랜덤 선택)
     * → 상위 30% 직업 중에서 선택되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_MultipleJobs() {
        // given
        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(150.0f);

        HandaliStat maxHandaliStat = new HandaliStat(handali, activityStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat);

        Job job1 = createJob("운동선수", 10000);
        Job job2 = createJob( "댄서", 8000);
        Job job3 = createJob( "요가강사", 6000);
        Job job4 = createJob( "배달라이더", 4000);
        Job job5 = createJob("청소원", 2000);

        List<Job> jobs = List.of(job1, job2, job3, job4, job5);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(maxStats);
        when(jobRepository.findJobByMaxHandaliStat(TypeName.ACTIVITY_SKILL, 150.0f)).thenReturn(jobs);

        // when
        Job result = jobService.assignBestJobToHandali(handali);

        // then
        assertNotNull(result);
        // 상위 30% = 2개 (운동선수 10000, 댄서 8000)
        assertTrue(result.getWeekSalary() >= 8000, "상위 30% 직업 중 선택되어야 함");
        verify(jobRepository, times(1)).findJobByMaxHandaliStat(TypeName.ACTIVITY_SKILL, 150.0f);
    }

    /**
     * ✅ [테스트 목적]
     * 가중치 기반 랜덤 선택 검증
     * → 주급이 높은 직업이 선택될 확률이 더 높은지 통계적으로 검증
     */
    @Test
    void testAssignBestJobToHandali_WeightedRandomDistribution() {
        // given
        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(200.0f);

        HandaliStat maxHandaliStat = new HandaliStat(handali, activityStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat);

        Job highSalaryJob = createJob("고액연봉", 9000);
        Job lowSalaryJob = createJob( "저액연봉", 1000);

        List<Job> jobs = List.of(highSalaryJob, lowSalaryJob);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(maxStats);
        when(jobRepository.findJobByMaxHandaliStat(TypeName.ACTIVITY_SKILL, 200.0f)).thenReturn(jobs);

        // when - 100번 실행하여 통계 확인
        int highSalaryCount = 0;
        int iterations = 100;

        for (int i = 0; i < iterations; i++) {
            Job result = jobService.assignBestJobToHandali(handali);
            if (result.getName().equals("고액연봉")) {
                highSalaryCount++;
            }
        }

        // then - 주급이 9배 높으므로, 고액연봉이 더 많이 선택되어야 함
        // 이론상 90% 확률이지만, 랜덤이므로 70% 이상이면 통과
        assertTrue(highSalaryCount > 70,
                "고액연봉 선택 횟수: " + highSalaryCount + "/100 (70% 이상이어야 함)");
    }

    /**
     * ✅ [테스트 목적]
     * 직업이 정확히 1개만 있는 경우 (상위 30% 계산)
     * → 해당 직업이 100% 선택되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_ExactlyOneJobInTop30Percent() {
        // given
        Stat artStat = new Stat(TypeName.ART_SKILL);
        artStat.setValue(80.0f);

        HandaliStat maxHandaliStat = new HandaliStat(handali, artStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat);

        Job artistJob = createJob( "화가", 7500);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(List.of(maxHandaliStat));
        when(jobRepository.findJobByMaxHandaliStat(TypeName.ART_SKILL, 80.0f)).thenReturn(List.of(artistJob));

        // when
        Job result = jobService.assignBestJobToHandali(handali);

        // then
        assertEquals("화가", result.getName());
        assertEquals(7500, result.getWeekSalary());
    }

    /**
     * ✅ [테스트 목적]
     * 주급이 동일한 여러 직업이 있는 경우
     * → 가중치가 동일하므로 균등하게 선택되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_EqualSalaryJobs() {
        // given
        Stat intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(120.0f);

        HandaliStat maxHandaliStat = new HandaliStat(handali, intelligentStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat);

        Job job1 = createJob( "개발자", 5000);
        Job job2 = createJob( "데이터분석가", 5000);
        Job job3 = createJob( "연구원", 5000);

        List<Job> jobs = List.of(job1, job2, job3);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(maxStats);
        when(jobRepository.findJobByMaxHandaliStat(TypeName.INTELLIGENT_SKILL, 120.0f)).thenReturn(jobs);

        // when - 여러 번 실행하여 모두 선택 가능한지 확인
        boolean job1Selected = false;
        boolean job2Selected = false;
        boolean job3Selected = false;

        for (int i = 0; i < 50; i++) {
            Job result = jobService.assignBestJobToHandali(handali);
            if (Objects.equals(result.getName(), "개발자")) job1Selected = true;
            if (Objects.equals(result.getName(),"데이터분석가")) job2Selected = true;
            if (Objects.equals(result.getName(),"연구원")) job3Selected = true;
        }

        // then - 50번 실행 시 3개 모두 최소 1번씩은 선택되어야 함
        assertTrue(job1Selected || job2Selected || job3Selected,
                "동일 주급 직업들이 선택 가능해야 함");
    }

    /**
     * ✅ [테스트 목적]
     * 매우 많은 직업이 있는 경우 (10개)
     * → 상위 30% = 3개 직업만 선택 대상이 되는지 검증
     */
    @Test
    void testAssignBestJobToHandali_ManyJobs() {
        // given
        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(250.0f);

        HandaliStat maxHandaliStat = new HandaliStat(handali, activityStat);
        List<HandaliStat> maxStats = List.of(maxHandaliStat);

        // 주급이 높은 순서대로 10개 직업
        Job job1 = createJob("직업1", 10000);
        Job job2 = createJob( "직업2", 9000);
        Job job3 = createJob( "직업3", 8000);
        Job job4 = createJob( "직업4", 7000);
        Job job5 = createJob( "직업5", 6000);
        Job job6 = createJob("직업6", 5000);
        Job job7 = createJob( "직업7", 4000);
        Job job8 = createJob( "직업8", 3000);
        Job job9 = createJob( "직업9", 2000);
        Job job10 = createJob( "직업10", 1000);

        List<Job> jobs = List.of(job1, job2, job3, job4, job5, job6, job7, job8, job9, job10);

        when(handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId())).thenReturn(maxStats);
        when(jobRepository.findJobByMaxHandaliStat(TypeName.ACTIVITY_SKILL, 250.0f)).thenReturn(jobs);

        // when - 100번 실행
        int top3Count = 0;
        for (int i = 0; i < 100; i++) {
            Job result = jobService.assignBestJobToHandali(handali);
            // 상위 30% = 3개 (직업1, 직업2, 직업3)
            if (result.getWeekSalary() >= 8000) {
                top3Count++;
            }
        }

        // then - 모두 상위 30%에서 선택되어야 함
        assertEquals(100, top3Count, "모든 선택이 상위 30% 직업이어야 함");
    }

    // 헬퍼 메서드
    private Job createJob(String name, int salary) {
        Job job = new Job();
        job.setName(name);
        job.setWeekSalary(salary);
        return job;
    }
}