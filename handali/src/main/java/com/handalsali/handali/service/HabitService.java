package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
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
import java.util.List;

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


    // [개발자와 사용자가 설정한 습관 조회]
    public List<Habit> getHabitsByToken(String token, String category_type, String category) {
        // Token을 이용해 사용자 정보 조회
        User user = userService.tokenToUser(token);

        // String 타입의 category_type과 category를 각각 Enum으로 변환
        CreatedType createdTypeEnum = CreatedType.valueOf(category_type);
        Categoryname categoryNameEnum = Categoryname.valueOf(category);

        // 해당 사용자의 습관 조회
        return habitRepository.findByUserIdAndCategoryTypeAndCategory(user.getUserId(), createdTypeEnum, categoryNameEnum);
    }

    // [카테고리별 습관 조회]
    public List<HabitDTO.HabitByCategoryResponse> getHabitsByUserCategoryAndMonthByToken(String token, CreatedType createdType, Categoryname category, int month) {
        // Token을 이용해 사용자 정보 조회
        User user = userService.tokenToUser(token);

        // 해당 사용자의 카테고리별 습관 조회
        return habitRepository.findByUserCategoryAndMonth(user.getUserId(), createdType, category, month)
                .stream()
                .map(habit -> new HabitDTO.HabitByCategoryResponse(
                        habit.getHabitId(),
                        habit.getDetailedHabitName()
                ))
                .toList();
    }

}
