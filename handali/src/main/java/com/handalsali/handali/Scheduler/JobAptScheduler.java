package com.handalsali.handali.Scheduler;

import com.handalsali.handali.service.HandaliService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobAptScheduler {
    private final HandaliService handaliService;

    // 매월 1일 00:00:00에 자동 실행
    @Scheduled(cron = "0 0 0 1 * ?")  // "초 분 시 일 월 요일"
    public void processMonthlyJobAndApartmentEntry() {
        System.out.println("🔄 [자동 실행] 매월 1일: 한달이 취업 및 아파트 입주 시작...");

        handaliService.processMonthlyJobAndApartmentEntry();  // 취업 + 입주 실행

        System.out.println(" [완료] 한달이 취업 및 아파트 입주 완료.");
    }
}
