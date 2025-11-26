package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class JobService {
    private final JobRepository jobRepository;
    private final HandaliStatRepository handaliStatRepository;

    public JobService(JobRepository jobRepository,HandaliStatRepository handaliStatRepository) {
        this.jobRepository = jobRepository;
        this.handaliStatRepository = handaliStatRepository;
    }

    /**
     * 한달이의 최적 직업 할당
     **/
    public Job assignBestJobToHandali(Handali handali) {
        // 1. 가장 높은 스탯 찾기
        List<HandaliStat> maxStats = handaliStatRepository.findMaxStatByHandaliId(handali.getHandaliId());

        if (maxStats.isEmpty()) {
            return jobRepository.save(jobRepository.findByName("백수"));
        }

        int maxStatCnt = new Random().nextInt(maxStats.size());
        HandaliStat maxHandaliStat = maxStats.get(maxStatCnt);

        // 2. 해당 스탯과 비교하여 직업 리스트 가져오기
        List<Job> jobs = jobRepository.findJobByMaxHandaliStat(
                maxHandaliStat.getStat().getTypeName(),
                maxHandaliStat.getStat().getValue());

        // 3. 가능한 직업이 없으면 백수 할당
        if (jobs.isEmpty()) {
            return jobRepository.save(jobRepository.findByName("백수"));
        }

        // 4. 주급을 기반으로 가중치 랜덤 선택
        return selectJobByWeightedRandomTopPercent(jobs);

    }

    /**
     * 상위 3퍼센트의 직업 추출
     **/
    private Job selectJobByWeightedRandomTopPercent(List<Job> jobs) {

        // 1. 주급 기준 내림차순 정렬
        List<Job> sorted = jobs.stream()
                .sorted((a, b) -> Integer.compare(b.getWeekSalary(), a.getWeekSalary()))
                .toList();

        // 2. 상위 N% 직업만 추출 (최소 1개 보장)
        int topCount = Math.max(1, (int) Math.ceil(sorted.size() * 0.3));
        List<Job> topJobs = sorted.subList(0, topCount);

        // 3. 가중치 랜덤 선택
        return selectJobByWeightedRandom(topJobs);
    }

    /**
     * 가중치 기반 랜덤 직업 선택
     **/
    private Job selectJobByWeightedRandom(List<Job> jobs) {

        // 1. 전체 가중치(주급의 합) 계산
        int totalWeight = jobs.stream()
                .mapToInt(Job::getWeekSalary)
                .sum();

        // 2. 랜덤 값 생성 (0 ~ totalWeight)
        int randomWeight = (int) (Math.random() * totalWeight);

        // 3. 가중치 기반으로 직업 선택 - 주급이 높을 수록 넓은 범위
        int cumulativeWeight = 0;
        for (Job job : jobs) {
            cumulativeWeight += job.getWeekSalary();
            if (randomWeight < cumulativeWeight) {
                return job;
            }
        }

        // 기본값 (예외 발생 방지를 위해 마지막 직업 반환)
        return jobs.get(jobs.size() - 1);

    }

}
