package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.NoBlankException;
import com.handalsali.handali.service.HabitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //[카테고리, 사용자에 따른 습관 조회]
    @GetMapping("/category-user")
    public ResponseEntity<HabitDTO.getHabitsApiResponse> getHabitsByUser(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String category) {

        String token = baseController.extraToken(accessToken);

        HabitDTO.getHabitsApiResponse response=habitService.getHabitsByUser(token,category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //[카테고리, 개발자에 따른 습관 조회]
    @GetMapping("/category-dev")
    public ResponseEntity<HabitDTO.getHabitsApiResponse> getHabitsByDev(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String category){

        String token = baseController.extraToken(accessToken);

        HabitDTO.getHabitsApiResponse response=habitService.getHabitsByDev(token,category);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    //[카테고리,달에 따른 습관 조회]
    @GetMapping("/category-month")
    public ResponseEntity<Map<String, Object>> getHabitsByUserAndCategoryAndMonth(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam Categoryname category,
            @RequestParam int month) {

        String token = baseController.extraToken(accessToken);

        Map<String, Object> habits = habitService.getHabitsByUserAndCategoryAndMonth(token,category,month);

        return ResponseEntity.status(HttpStatus.OK).body(habits);
    }
}
