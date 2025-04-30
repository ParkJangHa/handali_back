package com.handalsali.handali.scheduler;

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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HandaliScheduler {

    private final HandaliRepository handaliRepository;
    private final JobService jobService;
    private final ApartmentService apartmentService;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;


    // 🎯 [1] 직업 부여 - 매월 1일 00:00:01 실행
    @Scheduled(cron = "1 0 0 1 * *", zone = "Asia/Seoul")
    @Transactional
    public void assignJobsToHandalis() {
        long startTime = System.nanoTime();
        System.out.println("🎯 [직업 부여 시작]");

        LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        List<Handali> handalis = handaliRepository.findUnemployedHandalisForMonth(start, end);
        int success = 0, fail = 0;

        for (Handali handali : handalis) {
            try {
                if (handali.getJob() == null) {
                    Job job = jobService.assignBestJobToHandali(handali);
                    handali.setJob(job);
                    handaliRepository.save(handali);
                    System.out.println("✅ 직업 부여: " + handali.getNickname() + " → " + job.getName());
                    success++;
                }
            } catch (Exception e) {
                fail++;
                System.out.println("❌ 직업 부여 실패 (" + handali.getNickname() + "): " + e.getMessage());
            }
        }
        long duration = System.nanoTime() - startTime;
        System.out.printf("🎯 [직업 부여 완료] 성공: %d, 실패: %d, 총 소요 시간: %.2f초\n",
                success, fail, duration / 1_000_000_000.0);
    }

    // 🏢 [2] 아파트 입주 - 매월 1일 00:01:00 실행
    @Scheduled(cron = "0 1 0 1 * *", zone = "Asia/Seoul")
    @Transactional
    public void assignApartmentsToHandalis() {
        long startTime = System.nanoTime();
        System.out.println("🏢 [아파트 입주 시작]");

        LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        List<Handali> handalis = handaliRepository.findUnemployedHandalisForMonth(start, end);
        int success = 0, fail = 0;

        for (Handali handali : handalis) {
            try {
                if (handali.getApart() == null) {
                    Apart apart = apartmentService.assignApartmentToHandali(handali);
                    handali.setApart(apart);
                    handaliRepository.save(handali);
                    System.out.println("✅ 아파트 입주: " + handali.getNickname() + " → " + apart.getApartId());
                    success++;
                }
            } catch (Exception e) {
                fail++;
                System.out.println("❌ 아파트 입주 실패 (" + handali.getNickname() + "): " + e.getMessage());
            }
        }

        long duration = System.nanoTime() - startTime;
        System.out.printf("🏢 [아파트 입주 완료] 성공: %d, 실패: %d, 총 소요 시간: %.2f초\n",
                success, fail, duration / 1_000_000_000.0);
    }

    // 💰 [3] 주급 지급 (기존과 동일)
    @Scheduled(cron="0 0 0 * * MON", zone = "Asia/Seoul")
    public void payWeekSalary() {
        List<Handali> handalis = handaliRepository.findAllByJobIsNotNull();

        for (Handali handali : handalis) {
            try {
                YearMonth startMonth = YearMonth.from(handali.getStartDate());
                YearMonth nowMonth = YearMonth.now();
                long diff = ChronoUnit.MONTHS.between(startMonth, nowMonth);
                double ratio = Math.max(0, 12 - diff) / 12.0;

                LocalDate startDate = startMonth.atDay(1);
                LocalDate endDate = startMonth.atEndOfMonth();
                int recordCount = recordRepository.countByUserAndDate(handali.getUser(), startDate, endDate);
                int baseSalary = handali.getJob().getWeekSalary();
                int total = recordCount * 10 + (int) (baseSalary * ratio);

                User user = handali.getUser();
                user.setTotal_coin(user.getTotal_coin() + total);
                userRepository.save(user);

                System.out.println(
                        "\n💰 [주급 지급] " +
                                "👤 사용자 ID: " + user.getUserId() +
                                ", 🎈 한달이: " + handali.getNickname() +
                                ", 🏢 직업: " + handali.getJob().getName() +
                                ", 💰 지급 코인: " + total +
                                ", 💳 총 코인: " + user.getTotal_coin()
                );

            } catch (Exception e) {
                System.out.println("❗ 주급 지급 실패: " + handali.getNickname() + " | " + e.getMessage());
            }
        }
    }
}
