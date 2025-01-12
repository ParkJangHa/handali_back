package com.handalsali.handali.service;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.Record;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.TodayHabitAlreadyRecordException;
import com.handalsali.handali.repository.RecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class RecordService {
    private final UserService userService;
    private final HabitService habitService;
    private final RecordRepository recordRepository;
    public RecordService(UserService userService, HabitService habitService, RecordRepository recordRepository){
        this.userService=userService;
        this.habitService=habitService;
        this.recordRepository=recordRepository;
    }

    //[습관 기록]
    public Record recordTodayHabit(String token, Categoryname categoryName, String detailedHabitName,
                                   float time, int satisfaction, LocalDate date){
        //1. 사용자 확인
        User user=userService.tokenToUser(token);
        //2. 습관 아이디 확인
        Habit habit=habitService.findByCategoryAndDetailedHabitName(categoryName,detailedHabitName).
                orElseThrow(() -> new IllegalArgumentException("습관이 존재하지 않습니다."));
        //3. 습관 아이디&날짜 -> 하나의 습관은 하루에 한번만 기록 가능
        if (recordRepository.existsByHabitAndDate(habit, date)) {
            throw new TodayHabitAlreadyRecordException(
                    String.format("습관 '%s'은(는) %s에 이미 기록되었습니다.", detailedHabitName, date));
        }
        //4. 습관을 저장
        Record record=new Record(user,habit,time,satisfaction,date);
        recordRepository.save(record);

        return record;
    }
}
