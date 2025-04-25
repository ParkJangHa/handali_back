package com.handalsali.handali.service;

import com.handalsali.handali.DTO.RecordDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.Record;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.HabitNotExistsException;
import com.handalsali.handali.exception.TodayHabitAlreadyRecordException;
import com.handalsali.handali.repository.RecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Service
@Transactional
public class RecordService {
    private final UserService userService;
    private final HabitService habitService;
    private final RecordRepository recordRepository;
    private final StatService statService;

    public RecordService(UserService userService, HabitService habitService, RecordRepository recordRepository, StatService statService){
        this.userService=userService;
        this.habitService=habitService;
        this.recordRepository=recordRepository;
        this.statService = statService;
    }

    /**[습관 기록] 및 스탯 업데이트*/
    public RecordDTO.recordTodayHabitResponse recordTodayHabit(String token, RecordDTO.recordTodayHabitRequest request){
        //1. 사용자 확인
        User user=userService.tokenToUser(token);

        //2. 습관 아이디 확인
        Habit habit;
        habit = habitService.findByCategoryAndDetailedHabitName(request.getCategory(),request.getDetailed_habit_name()).
                orElseThrow(HabitNotExistsException::new);

        //3. 습관 아이디&날짜 -> 하나의 습관은 하루에 한번만 기록 가능
        if (recordRepository.existsByHabitAndDateAndUser(habit, request.getDate(),user)) {
            throw new TodayHabitAlreadyRecordException(
                    String.format("습관 '%s'은(는) %s에 이미 기록되었습니다.", request.getDetailed_habit_name(), request.getDate()));
        }

        //습관 기록하기 전, 이번달 기록 횟수 및 지난번 기록 시간 가져오기
        int recordCount = getRecordCount(user, habit);
        float lastRecordTime = getLastRecordTime(user, habit);

        //4. 습관을 저장
        Record record=new Record(user,habit, request.getTime(), request.getSatisfaction(),request.getDate());
        recordRepository.save(record); //트랜잭션 때문에 스탯 업데이트가 실패하면 기록도 저장안되지만, 기록아이디는 ai라서 증가 되어 있음

        //5. 스탯 업데이트
        boolean isChange=statService.statUpdateAndCheckHandaliStat(user,recordCount,lastRecordTime,request);

        return new RecordDTO.recordTodayHabitResponse(
                record.getRecordId(),
                "습관이 성공적으로 기록되었습니다.",
                isChange);
    }

    private float getLastRecordTime(User user, Habit habit) {
        Record lastRecord = recordRepository.findTopByUserAndHabitOrderByDateDesc(user, habit);
        float lastRecordedTime = (lastRecord != null) ? lastRecord.getTime() : 0f;
        System.out.println("lastRecordedTime: " + lastRecordedTime);

        return lastRecordedTime;
    }

    private int getRecordCount(User user, Habit habit) {
        // 이번달의 기록 횟수
        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(now);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        int recordedDays = recordRepository.countByUserAndHabitAndDate(user, habit, startDate, endDate);
        System.out.println("recordDats: " + recordedDays);

        return recordedDays;
    }
}
