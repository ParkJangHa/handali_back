package com.handalsali.handali.service;

import com.handalsali.handali.DTO.JobStatDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class JobService {
    private final JobRepository jobRepository;
    private final HandaliStatService handaliStatService;
    private final HandaliService handaliService;
    private final UserService userService;

    public JobService(JobRepository jobRepository, HandaliStatService handaliStatService, HandaliService handaliService, UserService userService) {
        this.jobRepository = jobRepository;
        this.handaliStatService = handaliStatService;
        this.handaliService = handaliService;
        this.userService = userService;
    }

    //[취업]
    public JobStatDTO.JobResponse assignBestJobToHandali(String token, Long handaliId) {
        //1. 사용자 확인
        User user=userService.tokenToUser(token);

        // Handali Handali ="한달이를 찾을 수 있는 메서드 추가"

        //2. 한달이의 스탯중 가장 높은 값 찾기, 모든 값이 동일할 경우 가장 먼저 가져온 값을 사용
        HandaliStat maxHandaliStat = handaliStatService.findMaxStatByHandaliId(handaliId).get(0);

        //3. 스탯값과 비교하여 큰 값에 해당하는 직업만 가져오기
        List<Job> jobs=jobRepository.findJobByMaxHandaliStat(
                maxHandaliStat.getStat().getTypeName(),
                maxHandaliStat.getStat().getValue());

        Job job;
        if (jobs.isEmpty()) {
            // 4. 직업 리스트가 비어있으면 직업은 백수
            job=jobRepository.findByName("백수");
        }else{
            // 5. 주급을 기반으로 가중치 랜덤 선택
            job = selectJobByWeightedRandom(jobs);
        }

        //6. 한달이에 직업 주기
        Handali handali = handaliService.findHandaliByCurrentDateAndUser(user);
        if (handali == null) {
            throw new HandaliNotFoundException("해당 유저의 이번달 한달이를 찾을 수 없습니다.");
        }
        handali.setJob(job);
        handaliService.save(handali);

        //7. DTO로 변환하여 반환
        JobStatDTO.JobStat jobStatDTO=new JobStatDTO.JobStat(
                maxHandaliStat.getStat().getTypeName(),
                maxHandaliStat.getStat().getValue());

        return new JobStatDTO.JobResponse(
                job.getWeekSalary(),
                job.getName(),
                jobStatDTO
        );
    }

    // 가중치 기반 랜덤 선택
    private Job selectJobByWeightedRandom(List<Job> jobs) {
        // 1. 전체 가중치(주급의 합) 계산
        int totalWeight = jobs.stream()
                .mapToInt(Job::getWeekSalary)
                .sum();

        // 2. 랜덤 값 생성 (0 ~ totalWeight)
        int randomWeight = (int) (Math.random() * totalWeight);

        // 3. 가중치 기반으로 직업 선택
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
