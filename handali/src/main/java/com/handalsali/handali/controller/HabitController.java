package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import com.handalsali.handali.exception.NoBlankException;
import com.handalsali.handali.service.HabitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
public class HabitController {
    private HabitService habitService;
    private BaseController baseController;

    public HabitController(HabitService habitService, BaseController baseController) {
        this.habitService = habitService;
        this.baseController = baseController;
    }

    @PostMapping("/habits")
    public ResponseEntity<HabitDTO.AddHabitApiResponse> addHabitsForCurrentMonth(@RequestHeader("Authorization") String accessToken,
                                                                                 @RequestBody HabitDTO.AddHabitRequest request){
        String token = baseController.extraToken(accessToken);

        if(request.getCategory()==null || request.getDetails()==null || request.getCreated_type()==null){
            throw new NoBlankException("카테고리, 세부사항, 생성자를 입력해주세요.");
        }

        Habit habit = habitService.addHabitsForCurrentMonth(
                token,
                request.getCategory(),
                request.getDetails(),
                request.getCreated_type());

        HabitDTO.AddHabitResponse habits = new HabitDTO.AddHabitResponse(
                request.getCategory(),
                request.getDetails(),
                request.getCreated_type());

        HabitDTO.AddHabitApiResponse response=new HabitDTO.AddHabitApiResponse("습관이 성공적으로 추가되었습니다.",habits);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // [개발자와 사용자가 설정한 습관 조회]
    @GetMapping
    public ResponseEntity<?> getHabits(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam CreatedType category_type,
            @RequestParam Categoryname category) {
        String token = baseController.extraToken(accessToken);

        // Service에서 token을 이용해 user_id 판별 후 조회
        List<Habit> habits = habitService.getHabitsByToken(token, category_type.name(), category.name());

        // DTO 변환
        List<HabitDTO.DeveloperHabitResponse.HabitDetail> habitDetails = habits.stream()
                .map(habit -> new HabitDTO.DeveloperHabitResponse.HabitDetail(
                        habit.getHabitId(),
                        habit.getDetailedHabitName()
                ))
                .toList();

        HabitDTO.DeveloperHabitResponse response = new HabitDTO.DeveloperHabitResponse(
                category.name(),
                habitDetails
        );

        return ResponseEntity.ok(response);
    }

    // [카테고리별 습관 조회]
    @GetMapping("/category")
    public ResponseEntity<?> getHabitsByUserCategoryAndMonth(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam CreatedType created_type,
            @RequestParam Categoryname category,
            @RequestParam int month) {

        String token = baseController.extraToken(accessToken);

        // Service에서 token을 이용해 user_id 판별 후 조회
        List<HabitDTO.HabitByCategoryResponse> habits = habitService.getHabitsByUserCategoryAndMonthByToken(token, created_type, category, month);

        // DTO로 응답 데이터 구성
        HabitDTO.HabitsByCategoryResponse response = new HabitDTO.HabitsByCategoryResponse(
                category,
                month,
                habits
        );
        return ResponseEntity.ok(response);
    }
}
