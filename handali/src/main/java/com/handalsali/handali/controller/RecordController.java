package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.RecordDTO;
import com.handalsali.handali.domain.Record;
import com.handalsali.handali.service.RecordService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RecordController {
    private RecordService recordService;
    private BaseController baseController;
    public RecordController(RecordService recordService, BaseController baseController){
        this.recordService=recordService;
        this.baseController = baseController;
    }

    //[습관 추가]
    @PostMapping("/habits/record")
    public ResponseEntity<RecordDTO.recordTodayHabitResponse> recordTodayHabit(@RequestHeader("Authorization") String accessToken,
                                                                               @RequestBody RecordDTO.recordTodayHabitRequest request){
        String token=baseController.extraToken(accessToken);
        Record record=recordService.recordTodayHabit(token,request.getCategory(), request.getDetailed_habit_name(),
                request.getTime(),request.getSatisfaction(),request.getDate());

        RecordDTO.recordTodayHabitResponse response=new RecordDTO.recordTodayHabitResponse(record.getRecordId(),"습관이 성공적으로 기록되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
