package com.handalsali.handali.service;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import com.handalsali.handali.repository.HabitRepository;
import com.handalsali.handali.repository.UserHabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class HabitService {
    private UserService userService;
    private HabitRepository habitRepository;
    private UserHabitRepository userHabitRepository;

    public HabitService(UserService userService, HabitRepository habitRepository, UserHabitRepository userHabitRepository) {
        this.userService = userService;
        this.habitRepository = habitRepository;
        this.userHabitRepository = userHabitRepository;
    }

    //이번달 초기 습관 추가 및 설정
    public Habit addHabitsForCurrentMonth(String token, Categoryname categoryName, String details, CreatedType createdType){
        //1. 사용자 확인
        User user=userService.tokenToUser(token);

        //2. 습관 추가
        Habit habit=habitRepository.findByCategoryNameAndDetailedHabitName(categoryName,details).orElseGet( //이미 있는 습관은 넘어 가고 없으면 추가
                () -> { Habit newHabit = new Habit(categoryName, details, createdType);
                        return habitRepository.save(newHabit);}
        );

        //3. 사용자-습관 테이블에 추가
        int currentMonth=LocalDate.now().getMonthValue();
        if(userHabitRepository.existsByUserAndHabit(user,habit)){ //(이미 추가했던 습관일 경우, month 만 갱신)
            UserHabit userHabit=userHabitRepository.findByUserAndHabit(user,habit);
            userHabit.setMonth(currentMonth);
            userHabitRepository.save(userHabit);
        }else{
            UserHabit userHabit=new UserHabit(user,habit,currentMonth);
            userHabitRepository.save(userHabit);
        }

        return habit;
    }
}
