package com.handalsali.handali.service;

import com.handalsali.handali.domain.Job;
import com.handalsali.handali.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job assignBestJobToHandali(Long handaliId) {
        List<Job> eligibleJobs = jobRepository.findEligibleJobs(handaliId);
        return eligibleJobs.stream()
                .max(Comparator.comparingInt(Job::getWeekSalary))
                .orElseThrow(() -> new IllegalArgumentException("요구 조건을 만족하는 직업이 없습니다."));
    }
}
