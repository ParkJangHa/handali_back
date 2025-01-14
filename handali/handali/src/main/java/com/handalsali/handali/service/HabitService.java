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

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HabitService {
    private final UserService userService;
    private final HabitRepository habitRepository;
    private final UserHabitRepository userHabitRepository;

    public HabitService(UserService userService, HabitRepository habitRepository, UserHabitRepository userHabitRepository) {
        this.userService = userService;
        this.habitRepository = habitRepository;
        this.userHabitRepository = userHabitRepository;
    }

    //이번달 초기 습관 추가 및 설정
    public Habit addHabitsForCurrentMonth(String token, Categoryname categoryName, String details, CreatedType createdType) {
        //1. 사용자 확인
        User user = userService.tokenToUser(token);

        //2. 습관 추가
        Habit habit = habitRepository.findByCategoryNameAndDetailedHabitName(categoryName, details).orElseGet( //이미 있는 습관은 넘어 가고 없으면 추가
                () -> {
                    Habit newHabit = new Habit(categoryName, details, createdType);
                    return habitRepository.save(newHabit);
                }
        );

        //3. 사용자-습관 테이블에 추가
        int currentMonth = LocalDate.now().getMonthValue();
        if (userHabitRepository.existsByUserAndHabit(user, habit)) { //(이미 추가했던 습관일 경우, month 만 갱신)
            UserHabit userHabit = userHabitRepository.findByUserAndHabit(user, habit);
            userHabit.setMonth(currentMonth);
            userHabitRepository.save(userHabit);
        } else {
            UserHabit userHabit = new UserHabit(user, habit, currentMonth);
            userHabitRepository.save(userHabit);
        }

        return habit;
    }

    //습관 조회 추가
    public List<Habit> getUserHabits(Long user_id, String category_type, String category) {
        // String 타입의 category_type과 category를 각각 Enum으로 변환
        CreatedType createdTypeEnum = CreatedType.valueOf(category_type);
        Categoryname categoryNameEnum = Categoryname.valueOf(category);

        List<Habit> habits = habitRepository.findByUserIdAndCategoryTypeAndCategory(user_id, createdTypeEnum, categoryNameEnum);
        System.out.println("Fetched Habits: " + habits);
        return habits;
    }
    //String은 자유로운 값 입력 가능, Enum은 정해진 값만 허용
    //따라서 코드 안정성, 가독성, 유지보수성 향상


    //카테고리별 습관 추가
    public List<Map<String, Object>> getHabitsByUserCategoryAndMonth(Long user_id, CreatedType createdType, Categoryname category, int month) {
        return habitRepository.findByUserCategoryAndMonth(user_id, createdType, category, month)
                .stream()
                .map(habit -> Map.of(
                        "habit_id", (Object) habit.getHabitId(),
                        "detailed_habit_name", (Object) habit.getDetailedHabitName()
                ))
                .toList();
    }
}
