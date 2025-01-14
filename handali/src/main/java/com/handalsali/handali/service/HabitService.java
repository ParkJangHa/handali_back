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

import java.time.LocalDate;
import java.util.Optional;
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


    //[습관 추가] 이번달 초기 습관 추가 및 설정
    public Habit addHabitsForCurrentMonth(String token, Categoryname categoryName, String details, CreatedType createdType){
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

    //카테고리, 세부습관으로 습관 찾기
    public Optional<Habit> findByCategoryAndDetailedHabitName(Categoryname categoryname, String detailedHabitName){
        return habitRepository.findByCategoryNameAndDetailedHabitName(categoryname,detailedHabitName);
    }

    //[사용자, 카테고리별 습관 조회]
    public HabitDTO.getHabitsApiResponse getHabitsByUser(String token,String category) {
        User user = userService.tokenToUser(token);

        try{
            Categoryname categoryNameEnum = Categoryname.valueOf(category);

            List<Habit> habits = habitRepository.findByUserAndCreatedTypeAndCategory(user,CreatedType.USER,categoryNameEnum);
            return mapToHabitsApiResponse(category, habits);

        } catch (IllegalArgumentException e){
            throw new CreatedTypeOrCategoryNameWrongException();
        }
    }

    //[개발자, 카테고리별 습관 조회]
    public HabitDTO.getHabitsApiResponse getHabitsByDev(String token,String category){
        User user=userService.tokenToUser(token);

        try {
            Categoryname categoryNameEnum = Categoryname.valueOf(category);

            List<Habit> habits = habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryNameEnum);
            return mapToHabitsApiResponse(category, habits);

        } catch (IllegalArgumentException e) {
            throw new CreatedTypeOrCategoryNameWrongException();
        }
    }

    //습관 조회 응답 형식
    private HabitDTO.getHabitsApiResponse mapToHabitsApiResponse(String category, List<Habit> habits) {
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
    }



    //[달, 카테고리별 습관 조회]
    public Map<String,Object> getHabitsByUserAndCategoryAndMonth(String token, Categoryname category, int month) {
        User user = userService.tokenToUser(token);

        List<Habit> habits=habitRepository.findByUserAndCategoryAndMonth(user,category,month);

        List<Map<String, Object>> habitsResponse = habits.stream()
                .map(habit -> Map.of(
                        "habit_id", (Object) habit.getHabitId(),
                        "detail", habit.getDetailedHabitName()
                ))
                .toList();

        return Map.of(
                "category",category.name(),
                "habits",habitsResponse
        );
    }
}
