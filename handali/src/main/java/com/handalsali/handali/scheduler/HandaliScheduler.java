package com.handalsali.handali.scheduler;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.RecordRepository;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.service.ApartmentService;
import com.handalsali.handali.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HandaliScheduler {

    private final HandaliRepository handaliRepository;
    private final JobService jobService;
    private final ApartmentService apartmentService;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    /** [매월 1일 자동 실행] 현재 키우고 있는 한달이들 취업 + 입주 처리*/
    @Scheduled(cron = "1 0 0 1 * *", zone = "Asia/Seoul")
//    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
    @Transactional
    public void processMonthlyJobAndApartmentEntry() {
        try{
            LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1);  // 다음 달 1일

            System.out.println("🗓️ 처리 기간: " + startOfMonth + " ~ " + endOfMonth.minusDays(1));

            List<Handali> handalis = handaliRepository.findUnemployedHandalisForMonth(
                    startOfMonth,   // >= 10-01 00:00:00
                    endOfMonth      // <  11-01 00:00:00
            );

            System.out.println("🔍 발견된 한달이 수: " + handalis.size());

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
        }catch(Exception e){
            System.err.println("❌ 스케줄러 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
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

    /**직업에 따른 주급 사용자에게 지급
     * 한달 기록 횟수*10 + 주급(12달이 지나면 지급량 없음)
     * */
    @Scheduled(cron="0 0 0 * * MON")
//    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
    public void payWeekSalary(){

        List<Handali> handalis = handaliRepository.findAllByJobIsNotNull();

        for (Handali handali : handalis) {
            //1. 한달이의 년월 및 주급 감소량 찾기
            LocalDate handaliNow=handali.getStartDate();
            YearMonth startYearMonth = YearMonth.from(handaliNow); //한달이 시작 년월
            YearMonth currentYearMonth=YearMonth.now(); //현재 년월

            long diffMonth= ChronoUnit.MONTHS.between(startYearMonth, currentYearMonth);
            double salaryRatio = Math.max(0, 12-diffMonth) / 12.0; //1.0~0.0

            //2. 한달이의 사용자 찾기
            User user = handali.getUser();

            int totalSalary = calculateSalaryFor(handali);

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

    public int calculateSalaryFor(Handali handali) {
        YearMonth startYearMonth = YearMonth.from(handali.getStartDate());
        long diffMonth = ChronoUnit.MONTHS.between(startYearMonth, YearMonth.now());
        double salaryRatio = Math.max(0, 12 - diffMonth) / 12.0;

        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        int recordCnt = recordRepository.countByUserAndDate(handali.getUser(), startDate, endDate);
        int weekSalary = handali.getJob().getWeekSalary();

        return recordCnt * 10 + (int) (weekSalary * salaryRatio);
    }
}
