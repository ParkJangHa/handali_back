package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.MoreOneLessThreeSelectException;
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

    //[습관추가]
    @PostMapping
    public ResponseEntity<?> addHabitsForCurrentMonth(@RequestHeader("Authorization") String accessToken,
                                                      @RequestBody HabitDTO.AddHabitApiRequest request){
        String token = baseController.extraToken(accessToken);

        if(request.getHabits() == null || request.getHabits().isEmpty()) {
            throw new MoreOneLessThreeSelectException("하나 이상의 습관을 선택해주세요.");
        }
        if(request.getHabits().size()>3){
            throw new MoreOneLessThreeSelectException("습관은 최대 3개까지 선택할 수 있습니다.");
        }

        List<HabitDTO.AddHabitResponse> addHabitResponse=habitService.addHabitsForCurrentMonth(token,request);

        HabitDTO.AddHabitApiResponse response=new HabitDTO.AddHabitApiResponse("습관이 성공적으로 추가되었습니다.",addHabitResponse);
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
