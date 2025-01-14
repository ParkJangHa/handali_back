package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import com.handalsali.handali.exception.CreatedTypeOrCategoryNameWrongException;
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

    //[개발자, 사용자 별 습관 조회]
    public HabitDTO.getHabitsApiResponse getUserHabits(String token, String created_type, String category) {
        User user = userService.tokenToUser(token);
        // String 타입의 category_type과 category를 각각 Enum으로 변환
        try{
            CreatedType createdTypeEnum = CreatedType.valueOf(created_type);
            Categoryname categoryNameEnum = Categoryname.valueOf(category);

            List<Habit> habits = habitRepository.findByUserAndCreatedTypeAndCategory(user,createdTypeEnum,categoryNameEnum);
            // 변환 로직: List<Habit> -> getHabitsApiResponse
            HabitDTO.getHabitsApiResponse response = new HabitDTO.getHabitsApiResponse();
            response.setCategory(category);

            List<HabitDTO.getHabitResponse> habitResponses = habits.stream()
                    .map(habit -> {
                        HabitDTO.getHabitResponse habitResponse = new HabitDTO.getHabitResponse();
                        habitResponse.setDetail(habit.getDetailedHabitName());
                        return habitResponse;
                    })
                    .toList();

            response.setHabits(habitResponses);
            return response;
        } catch (IllegalArgumentException e){
            throw new CreatedTypeOrCategoryNameWrongException();
        }
    }


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
