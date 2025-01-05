package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.exception.NoBlankException;
import com.handalsali.handali.service.HabitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
