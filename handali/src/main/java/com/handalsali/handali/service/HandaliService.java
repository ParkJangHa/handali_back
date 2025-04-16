package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.StatDetailDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.*;
import com.handalsali.handali.repository.*;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.JoinColumn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class HandaliService {
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private UserService userService;
    private HandaliRepository handaliRepository;
    private StatService statService;
    private HandaliStatRepository handaliStatRepository;
    private JobService jobService;
    private ApartmentService apartmentService;

    public HandaliService(UserService userService, HandaliRepository handaliRepository, StatService statService, HandaliStatRepository handaliStatRepository, JobService jobService, ApartmentService apartmentService, RecordRepository recordRepository, UserRepository userRepository) {
        this.userService = userService;
        this.handaliRepository = handaliRepository;
        this.statService = statService;
        this.handaliStatRepository = handaliStatRepository;
        this.jobService = jobService;
        this.apartmentService = apartmentService;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    /**[한달이 생성]*/
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
        statService.statInit(user,handali);

        return handali;
    }

    /**유저의 이번달 한달이 조회 - 다음 달로 넘어가는 순간 호출되면 한달이를 찾을 수 없는 예외 발생*/
    public Handali findHandaliByCurrentDateAndUser(User user){
        Handali handali = handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId());
        return handali;
    }

    /**한달이 찾고, [스탯 업데이트]*/
    public boolean statUpdate(User user, Categoryname categoryname, int recordCount, float lastRecordTime,float time, int satisfaction) {
        // 1. 한달이 찾기
        Handali handali = findHandaliByCurrentDateAndUser(user);
        if (handali == null) throw new HandaliNotFoundException("한달이를 찾을 수 없습니다.");

        // 2. StatService로 한달이 객체 전달
        return statService.statUpdateAndCheckHandaliStat(handali, categoryname, recordCount,lastRecordTime, time, satisfaction);
    }

    /**[한달이 상태 변화]-이미지 반환*/
    public String changeHandali(String token){
        //1. 사용자 확인
        User user=userService.tokenToUser(token);

        //2. 한달이 찾기
        Handali handali = findHandaliByCurrentDateAndUser(user);

        //3. 이미지 생성 - image_활동_지능_예술.png
        StringBuilder imageName= new StringBuilder("image");
        List<HandaliStat> handaliStats=handaliStatRepository.findByHandali(handali);
        for(HandaliStat handaliStat:handaliStats){
            int level=statService.checkHandaliStat(handaliStat.getStat().getValue());
            imageName.append("_").append(level);
        }
        imageName.append(".png");

        String resultImage=imageName.toString();

        //4. handali 테이블에 변경된 이미지 저장
        handali.setImage(resultImage);
        handaliRepository.save(handali);
        return resultImage;
    }

    /** [한달이 상태 조회]*/
    public HandaliDTO.HandaliStatusResponse getHandaliStatusByMonth(String token) {
        User user = userService.tokenToUser(token);

        Handali handali = findHandaliByCurrentDateAndUser(user);
        if(handali==null){throw new HandaliNotFoundException("한달이가 존재하지 않습니다.");}

        int days_Since_Created = Period.between(handali.getStartDate(), LocalDate.now()).getDays()+1;


        return new HandaliDTO.HandaliStatusResponse(
                handali.getNickname(),
                days_Since_Created,
                handali.getUser().getTotal_coin(),
                handali.getImage()
        );

    }

    /** [스탯 조회]*/
    public HandaliDTO.StatResponse getStatsByHandaliId(Long handaliId, String token) {
        // Handali 엔티티 존재 여부 확인 (예외 처리 포함)
        handaliRepository.findById(handaliId)
                .orElseThrow(() -> new EntityNotFoundException("해당 handali_id에 대한 데이터가 없습니다."));

        // 스탯 조회
        List<StatDetailDTO> stats = handaliRepository.findStatsByHandaliId(handaliId);

        return new HandaliDTO.StatResponse(stats);
    }

//     매달 1일 오전 00:00:01(한국시간) 자동 실행
//    @Scheduled(cron = "1 0 0 1 * *", zone = "Asia/Seoul")
//    public void runMonthlyJobAndApartmentEntry() {
//        System.out.println("🚀 [자동 실행] 매달 1일 한달이 취업 및 아파트 입주 실행");
//        processMonthlyJobAndApartmentEntry();
//    }

    @Scheduled(cron = "*/5 * * * * *", zone = "Asia/Seoul")
    public void runMonthlyJobAndApartmentEntry() {
        System.out.println("🚀 [자동 실행]5초 마다 자동 입주 실행");
        processMonthlyJobAndApartmentEntry();
    }

    /** [매월 1일 자동 실행] 현재 키우고 있는 한달이들 취업 + 입주 처리*/
    @Transactional
    public void processMonthlyJobAndApartmentEntry() {
//        ----------------생성 달 기준 전달 한달이만 적용-----------------
        LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

        //(test) 해당 년도 사이에 존재하는 모든 한달이가 한꺼번에 추가됨
//        LocalDate startOfMonth = LocalDate.of(2025, 1, 1);
//        LocalDate startOfNextMonth = LocalDate.of(2025, 12, 31);

        System.out.println("🗓️ startOfMonth: " + startOfMonth);
        System.out.println("🗓️ endOfMonth: " + startOfNextMonth);

        List<Handali> handalis = handaliRepository.findUnemployedHandalisForMonth(startOfMonth, startOfNextMonth);
        System.out.println("🔍 처리 대상 한달이 수: " + handalis.size());

        if (handalis.isEmpty()) {
            System.out.println("⚠️ 이번 달에 취업 및 입주할 한달이가 없습니다.");
            return;
        }

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

    /** 한달이 취업 및 아파트 입주 **/
    @Transactional
    public void processEmploymentAndMoveIn(Handali handali) {
        if (handali == null) {
            throw new IllegalArgumentException("한달이 객체가 null입니다.");
        }

        if (handali.getJob() == null && handali.getApart() == null) {
            Job job = jobService.assignBestJobToHandali(handali);
            handali.setJob(job);

            Apart assignedApartment = apartmentService.assignApartmentToHandali(handali);
            handali.setApart(assignedApartment);

            handaliRepository.save(handali);

            System.out.println("✅ 취업 및 아파트 입주 완료: " + handali.getNickname() +
                    " | 직업: " + handali.getJob().getName() +
                    " | 아파트: " + handali.getApart().getApartId() +
                    " | 층수: " + handali.getApart().getFloor());
        }
        //예외처리 가능
    }

    /**[마지막 생성 한달이 조회]*/
    public HandaliDTO.RecentHandaliResponse getRecentHandali(String token) {
        // 사용자 인증
        User user = userService.tokenToUser(token);
        if (user == null) {
            throw new HandaliNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 가장 최근 생성된 한달이 찾기 (없으면 예외 발생)
        return handaliRepository.findLatestHandaliByUser(user.getUserId())
                .map(handali
                        -> {
                    String jobName = (handali.getJob() != null) ? handali.getJob().getName() : "미취업";
                    int weekSalary = (handali.getJob() != null) ? handali.getJob().getWeekSalary() : 0;

                    return new HandaliDTO.RecentHandaliResponse(
                            handali.getNickname(),
                            handali.getHandaliId(),
                            handali.getStartDate(),
                            jobName,
                            weekSalary,
                            handali.getImage()
                    );
                }).orElseThrow(() -> new HandaliNotFoundException("최근 생성된 한달이가 없습니다."));
    }

    /**직업에 따른 주급 사용자에게 지급
     * 한달 기록 횟수*10 + 주급(12달이 지나면 지급량 없음)
     * */
    @Scheduled(cron="0 0 0 * * MON")
//    @Scheduled(cron = "*/5 * * * * *", zone = "Asia/Seoul")
    public void payWeekSalary(){

        List<Handali> handalis = handaliRepository.findAllByJobIsNotNull();

        for (Handali handali : handalis) {
            //1. 한달이의 년월 및 주급 감소량 찾기
            LocalDate handaliNow=handali.getStartDate();
            YearMonth startYearMonth = YearMonth.from(handaliNow); //한달이 시작 년월
            YearMonth currentYearMonth=YearMonth.now(); //현재 년월

            long diffMonth= ChronoUnit.MONTHS.between(startYearMonth, currentYearMonth);
            double salaryRatio = Math.max(0, 12-diffMonth) / 12.0; //1.0~0.0

            LocalDate startDate = startYearMonth.atDay(1); //한달이 달의 시작 년월일
            LocalDate endDate=startYearMonth.atEndOfMonth(); //한달이 달의 마지막 년월일

            //2. 한달이의 사용자 찾기
            User user = handali.getUser();

            //3. 기록횟수 구하기
            int recordCnt = recordRepository.countByUserAndDate(user, startDate, endDate);

            //4. 한달이의 주급 구하기
            int weekSalary = handali.getJob().getWeekSalary();

            //5. 사용자에게 지급할 주급 계산하기
            int totalSalary = recordCnt * 10 + (int)(weekSalary*salaryRatio);

            //6. 저장하기
            user.setTotal_coin(user.getTotal_coin()+totalSalary);
            userRepository.save(user);

            System.out.println(
                    "\n📦 [주급 지급 완료] ==========================\n" +
                            "👤 사용자 ID       : " + user.getUserId() + "\n" +
                            "🎈 한달이 ID       : " + handali.getHandaliId() + "\n" +
                            "🏢 직업명          : " + handali.getJob().getName() + "\n" +
                            "📉 지급 비율       : " + String.format("%.0f%%", salaryRatio * 100) + "\n" +
                            "💰 지급된 주급     : " + totalSalary + " 코인\n" +
                            "💳 총 보유 코인    : " + user.getTotal_coin() + " 코인\n" +
                            "🕒 지급 일시       : " + LocalDateTime.now() + "\n" +
                            "============================================\n"
            );

        }
    }
}
