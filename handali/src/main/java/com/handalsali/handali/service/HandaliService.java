package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.StatDetailDTO;
import com.handalsali.handali.domain.*;
import com.handalsali.handali.repository.ApartRepository;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
public class HandaliService {
    private final HandaliStatService handaliStatService;
    private UserService userService;
    private HandaliRepository handaliRepository;
    private StatService statService;
    private final JobRepository jobRepository;
    private final ApartRepository apartRepository;

    public HandaliService(UserService userService, JobRepository jobRepository, HandaliRepository handaliRepository, ApartRepository apartRepository, StatService statService, HandaliStatService handaliStatService) {
        this.userService = userService;
        this.apartRepository = apartRepository;
        this.handaliRepository = handaliRepository;
        this.jobRepository = jobRepository;
        this.statService = statService;
        this.handaliStatService = handaliStatService;
    }

    //[한달이 생성]
    public Handali handaliCreate(String token,String nickname){
        //1. 사용자 인증
        User user=userService.tokenToUser(token);
        //2. 한달이는 한달에 한마리만 가능
        if(handaliRepository.countPetsByUserIdAndCurrentMonth(user)>0){
            throw new HanCreationLimitException();
        }
        //3. 한달이 생성
        Handali handali=new Handali(nickname, LocalDate.now(),user);
        handaliRepository.save(handali);

        //4. 한달이의 스탯 초기화
        statService.statInit(handali);

        return handali;
    }

    //유저의 이번달 한달이 조회 - 다음 달로 넘어가는 순간 호출되면 한달이를 찾을 수 없는 예외 발생
    public Handali findHandaliByCurrentDateAndUser(User user){
        Handali handali = handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId());
        return handali;
    }

    //한달이 찾고, [스탯 업데이트]
    public void statUpdate(User user, Categoryname categoryname, float time, int satisfaction) {
        // 1. 한달이 찾기
        Handali handali = findHandaliByCurrentDateAndUser(user);
        if (handali == null) throw new HandaliNotFoundException("한달이를 찾을 수 없습니다.");

        // 2. StatService로 한달이 객체 전달
        statService.statUpdate(handali, categoryname, time, satisfaction);
    }

    //한달이 저장
    public void save(Handali handali){
        handaliRepository.save(handali);
    }

    // [한달이 상태 조회]
    // 02-07 - 한달이 없을때 예외 처리 추가
    public HandaliDTO.HandaliStatusResponse getHandaliStatusByIdAndMonth(Long handaliId, String token) {
        userService.tokenToUser(token);

        //한달이 조회
        Handali handali = handaliRepository.findById(handaliId)
                .orElseThrow(() -> new HandaliNotFoundException("해당 한달이가 존재하지 않습니다."));

        // 생성일로부터 경과 일수를 계산하는 로직
        int days_Since_Created = Period.between(handali.getStartDate(), LocalDate.now()).getDays()+1;

        String message = "아직 30일이 되지 않았습니다.";
        if (days_Since_Created == 30) {
            message = "생성된지 30일이 되었습니다.";
        }

        return new HandaliDTO.HandaliStatusResponse(
                handali.getNickname(),
                days_Since_Created,
                message
        );

    }

    // [스탯 조회]
    public HandaliDTO.StatResponse getStatsByHandaliId(Long handaliId, String token) {
        // Handali 엔티티 존재 여부 확인 (예외 처리 포함)
        handaliRepository.findById(handaliId)
                .orElseThrow(() -> new EntityNotFoundException("해당 handali_id에 대한 데이터가 없습니다."));

        // 스탯 조회
        List<StatDetailDTO> stats = handaliRepository.findStatsByHandaliId(handaliId);

        return new HandaliDTO.StatResponse(stats);
    }

    // 매달 1일 오전 00:00:01(한국시간) 자동 실행
    //@Scheduled(cron = "1 0 0 1 * *", zone = "Asia/Seoul")
    //public void runMonthlyJobAndApartmentEntry() {
        //System.out.println("🚀 [자동 실행] 매달 1일 한달이 취업 및 아파트 입주 실행");
        //processMonthlyJobAndApartmentEntry();
    //}

    // [매월 1일 자동 실행] 현재 키우고 있는 한달이들 취업 + 입주 처리
    @Transactional
    public void processMonthlyJobAndApartmentEntry() {
        LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

        List<Handali> handalis = handaliRepository.findUnemployedHandalisForMonth(startOfMonth, startOfNextMonth);
        System.out.println("🔍 처리 대상 한달이 수: " + handalis.size());

        for (Handali handali : handalis) {
            System.out.println("🛠 처리 중: " + handali.getNickname() + " | 취업 여부: " +
                    (handali.getJob() != null ? "O" : "X") + " | 아파트 여부: " +
                    (handali.getApart() != null ? "O" : "X"));
            // [한달이 취업 및 아파트 입주 실행]
            processEmploymentAndMoveIn(handali);

            System.out.println("✅ 처리 완료: " + handali.getNickname() +
                    " | 직업: " + (handali.getJob() != null ? handali.getJob().getName() : "미취업") +
                    " | 아파트 ID: " + (handali.getApart() != null ? handali.getApart().getApartId() : "미입주"));

        }
    }

    /** [최신 한달이 조회] **/
    //public Handali findLatestHandaliByUser(User user) {
        //return handaliRepository.findLatestHandaliByUser(user)
                //.orElseThrow(() -> new HandaliNotFoundException("한달이를 찾을 수 없습니다."));
    //}

    /** 한달이 취업 및 아파트 입주 **/
    @Transactional
    public void processEmploymentAndMoveIn(Handali handali) {
        // 1. 취업 처리
        if (handali.getJob() == null) {
            Job job = assignBestJobToHandali(handali);
            job = jobRepository.save(job);
            handali.setJob(job);
            System.out.println("새 직업 부여됨");
        }

        // 2. 아파트 입주 처리 (기존에 입주한 아파트가 없을 경우만)
        Apart assignedApartment = assignApartmentToHandali(handali);
        handali.setApart(assignedApartment);

        // 3. 저장
        handaliRepository.save(handali);
        apartRepository.save(assignedApartment);

        // 4. 로그 확인
        System.out.println("✅ 취업 및 아파트 입주 완료: " + handali.getNickname() +
                " | 직업: " + handali.getJob().getName() +
                " | 아파트: " + assignedApartment.getApartId() +
                " | 층수: " + assignedApartment.getFloor());

    }

    /** 한달이의 최적 직업 할당 **/
    private Job assignBestJobToHandali(Handali handali) {
        // 1. 가장 높은 스탯 찾기
        List<HandaliStat> maxStats = handaliStatService.findMaxStatByHandaliId(handali.getHandaliId());

        if (maxStats.isEmpty()) {
            return jobRepository.save(jobRepository.findByName("백수"));
        }

        HandaliStat maxHandaliStat = maxStats.get(0);

        // 2. 해당 스탯과 비교하여 직업 리스트 가져오기
        List<Job> jobs = jobRepository.findJobByMaxHandaliStat(
                maxHandaliStat.getStat().getTypeName(),
                maxHandaliStat.getStat().getValue());

        // 3. 직업이 없으면 백수 할당
        if (jobs.isEmpty()) {
            return jobRepository.save(jobRepository.findByName("백수"));
        }

        // 4. 주급을 기반으로 가중치 랜덤 선택
        Job selectedJob = selectJobByWeightedRandom(jobs);

        return jobRepository.save(selectedJob);

    }

    /** 한달이의 아파트 배정 **/
    private Apart assignApartmentToHandali(Handali handali) {
        // 1. 최신 아파트 조회
        Apart latestApartment = apartRepository.findLatestApartment();

        // 2. 아파트가 없으면 새로 생성
        if (latestApartment == null) {
            latestApartment = new Apart(handali.getUser(), handali, handali.getNickname(), 1, 1L);
            apartRepository.save(latestApartment);
            System.out.println("새로운 아파트 생성: ID=1, 층수=1");
            return latestApartment;
        }

        // 3. 해당 아파트의 현재 최고층 확인
        Integer maxFloor = handaliRepository.findMaxFloorByApartment(latestApartment.getApartId().getApartId());
        if (maxFloor == null) {
            maxFloor = 0;
        }

        // 4. 12층 이하일 경우 기존 아파트에 입주
        if (maxFloor < 12) {
            Apart newApartmentEntry = new Apart(latestApartment.getUser(), handali, handali.getNickname(), maxFloor + 1, latestApartment.getApartId().getApartId());
            apartRepository.save(newApartmentEntry);
            System.out.println("기존 아파트 입주: ID=" + newApartmentEntry.getApartId() + ", 층수=" + (maxFloor + 1));
            return newApartmentEntry;
        }

        // 5. 12층 초과 시 새로운 아파트 생성
        Long newApartId = latestApartment.getApartId().getApartId() + 1;
        Apart newApartment = new Apart(handali.getUser(), handali, handali.getNickname(), 1, newApartId);
        apartRepository.save(newApartment);
        System.out.println("새로운 아파트 생성: ID=" + newApartment.getApartId());
        return newApartment;
    }

    /** 가중치 기반 랜덤 직업 선택 **/
    private Job selectJobByWeightedRandom(List<Job> jobs) {
        // 예외 처리: jobs 리스트가 비어있으면 "백수" 반환
        if (jobs == null || jobs.isEmpty()) {
            return jobRepository.findByName("백수");
        }

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
