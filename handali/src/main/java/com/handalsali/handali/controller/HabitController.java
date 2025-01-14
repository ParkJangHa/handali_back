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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/habits")
public class HabitController {
    private final HabitService habitService;
    private final BaseController baseController;

    public HabitController(HabitService habitService, BaseController baseController) {
        this.habitService = habitService;
        this.baseController = baseController;
    }

    @PostMapping
    public ResponseEntity<?> addHabitsForCurrentMonth(@RequestHeader("Authorization") String accessToken,
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

    //[습관 조회]
    @GetMapping("/habits")
    public HabitDTO.getHabitsApiResponse getHabits(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String created_type,
            @RequestParam String category) {

        String token = baseController.extraToken(accessToken);

        return habitService.getUserHabits(token,created_type,category);
    }

    //카테고리별 습관 추가
    @GetMapping("/{user_id}/{created_type}/{category}/{month}")
    public ResponseEntity<?> getHabitsByUserCategoryAndMonth(
            @PathVariable Long user_id,
            @PathVariable CreatedType created_type,
            @PathVariable Categoryname category,
            @PathVariable int month) {

        // 서비스 호출
        List<Map<String, Object>> habits = habitService.getHabitsByUserCategoryAndMonth(user_id, created_type, category, month);

        // 응답 데이터 구성
        return ResponseEntity.ok(Map.of(
                "user_id", user_id,
                "category", category.name(),
                "habits", habits
        ));
    }
}
