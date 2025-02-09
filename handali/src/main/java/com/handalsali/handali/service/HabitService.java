package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import com.handalsali.handali.exception.CreatedTypeOrCategoryNameWrongException;
import com.handalsali.handali.exception.HabitNotExistsException;
import com.handalsali.handali.repository.HabitRepository;
import com.handalsali.handali.repository.UserHabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.Map;



@Service
@Transactional
public class HabitService {
    private final UserService userService;
    private final HabitRepository habitRepository;
    private final UserHabitRepository userHabitRepository;
    public static final int MONTH_NOT_ASSIGNED = 0;

    public HabitService(UserService userService, HabitRepository habitRepository, UserHabitRepository userHabitRepository) {
        this.userService = userService;
        this.habitRepository = habitRepository;
        this.userHabitRepository = userHabitRepository;
    }

    /**[습관 추가] 사용자 습관 추가*/
    public void createUserHabit(String token,HabitDTO.AddHabitApiRequest addHabitApiRequest) {
//        setHabit(token, addHabitApiRequest,MONTH_NOT_ASSIGNED);

        //1. 사용자 확인
        User user = userService.tokenToUser(token);

        //2. 습관 추가
        for (HabitDTO.AddHabitRequest habitRequest : addHabitApiRequest.getHabits()) {
            Categoryname categoryName = habitRequest.getCategory();
            String detailedHabitName = habitRequest.getDetails();
            CreatedType createdType = habitRequest.getCreated_type();

            //2-1. 습관이 없으면 데이터베이스에 추가, 있으면 건너뜀
            Habit habit = habitRepository.findByCategoryNameAndDetailedHabitName(categoryName, detailedHabitName).orElseGet( //이미 있는 습관은 넘어 가고 없으면 추가
                    () -> {
                        Habit newHabit = new Habit(categoryName, detailedHabitName, createdType);
                        return habitRepository.save(newHabit);
                    }
            );

            //2-2. user-habit 테이블에 관계 추가
            if (!userHabitRepository.existsByUserAndHabit(user, habit)) { //이미 추가했던 습관일 경우 건너뛰고, 새로운 습관일 경우만 추가
                UserHabit userHabit = new UserHabit(user, habit);
                userHabitRepository.save(userHabit);
            }
        }
    }

    /**[이번달 습관으로 지정] 이번달에 실행할 습관으로 지정*/
    public void addHabitsForCurrentMonth(String token, HabitDTO.AddHabitApiRequest addHabitApiRequest){
            //1. 사용자 확인
            User user = userService.tokenToUser(token);

            //2. 습관 추가
            for (HabitDTO.AddHabitRequest habitRequest : addHabitApiRequest.getHabits()) {
                Categoryname categoryName = habitRequest.getCategory();
                String detailedHabitName = habitRequest.getDetails();
                CreatedType createdType = habitRequest.getCreated_type();

                //2-1. 습관이 없으면 오류
                Habit habit = habitRepository.findByCategoryNameAndDetailedHabitName(categoryName, detailedHabitName).orElseThrow(
                        ()->new HabitNotExistsException("습관이 존재하지 않습니다: "+detailedHabitName)
                );

                //2-2. user-habit 테이블에 관계 추가
                int currentMonth = LocalDate.now().getMonthValue();
                if (userHabitRepository.existsByUserAndHabit(user, habit)) { //이미 추가했던 습관일 경우, month 만 갱신
                    UserHabit userHabit = userHabitRepository.findByUserAndHabit(user, habit);
                    userHabit.setMonth(currentMonth);
                    userHabitRepository.save(userHabit);
                } else { //새로운 습관일 경우, 에러
                    throw new HabitNotExistsException("사용자가 해당 습관을 추가하지 않았습니다: "+detailedHabitName);
                }
            }
    }

//    /**습관을 데이터베이스에 추가*/
//    public void setHabit(String token, HabitDTO.AddHabitApiRequest addHabitApiRequest, int month) {
//        //1. 사용자 확인
//        User user = userService.tokenToUser(token);
//
//        //2. 습관 추가
//        for (HabitDTO.AddHabitRequest habitRequest : addHabitApiRequest.getHabits()) {
//            Categoryname categoryName = habitRequest.getCategory();
//            String detailedHabitName = habitRequest.getDetails();
//            CreatedType createdType = habitRequest.getCreated_type();
//
//            //2-1. 습관이 없으면 데이터베이스에 추가, 있으면 건너뜀
//            Habit habit = habitRepository.findByCategoryNameAndDetailedHabitName(categoryName, detailedHabitName).orElseGet( //이미 있는 습관은 넘어 가고 없으면 추가
//                    () -> {
//                        Habit newHabit = new Habit(categoryName, detailedHabitName, createdType);
//                        return habitRepository.save(newHabit);
//                    }
//            );
//
//            //2-2. user-habit 테이블에 관계 추가
////            int currentMonth = LocalDate.now().getMonthValue();
//            if (userHabitRepository.existsByUserAndHabit(user, habit)) { //이미 추가했던 습관일 경우, month 만 갱신
//                UserHabit userHabit = userHabitRepository.findByUserAndHabit(user, habit);
//                userHabit.setMonth(month);
//                userHabitRepository.save(userHabit);
//            } else { //새로운 습관일 경우, 데이터베이스에 추가
//                UserHabit userHabit = new UserHabit(user, habit, month);
//                userHabitRepository.save(userHabit);
//            }
//        }
//    }


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
