package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.service.HandaliService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/handalis")
public class HandaliController {
    private BaseController baseController;
    private HandaliService handaliService;

    public HandaliController(BaseController baseController,HandaliService handaliService){
        this.baseController=baseController;
        this.handaliService=handaliService;
    }

    /**[한달이 생성]*/
    @PostMapping
    public ResponseEntity<HandaliDTO.CreateHandaliResponse> handaliCreate(@RequestHeader("Authorization") String accessToken,
                                                @RequestBody HandaliDTO.CreateHandaliRequest request){

        String token=baseController.extraToken(accessToken);
        Handali handali=handaliService.handaliCreate(token,request.getNickname());
        HandaliDTO.CreateHandaliResponse createHandaliResponse=new HandaliDTO.CreateHandaliResponse(handali.getHandaliId(),handali.getNickname(),handali.getStartDate(),"한달이가 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(createHandaliResponse);
    }


    /**[한달이 상태 조회]*/
    @GetMapping("/view")
    public ResponseEntity<HandaliDTO.HandaliStatusResponse> getHandaliStatus(
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByMonth(token);

        return ResponseEntity.ok(response);
    }

    /** [스탯 조회]*/
    @GetMapping("/{handali_id}/stats")
    public ResponseEntity<HandaliDTO.StatResponse> getStatsByHandaliId(
            @PathVariable("handali_id") Long handaliId,
            @RequestHeader("Authorization") String accessToken) {

        // 토큰 처리
        String token = baseController.extraToken(accessToken);
        // 서비스 계층에서 스탯 데이터 가져오기
        HandaliDTO.StatResponse response = handaliService.getStatsByHandaliId(handaliId, token);

        return ResponseEntity.ok(response);
    }

    /**[한달이 상태 변화]*/
    @GetMapping("/change")
    public ResponseEntity<String> changeHandali(@RequestHeader("Authorization") String accessToken){
        String token = baseController.extraToken(accessToken);

        return ResponseEntity.ok(handaliService.changeHandali(token));
    }

}
