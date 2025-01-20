package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.service.HandaliService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HandaliController {
    private BaseController baseController;
    private HandaliService handaliService;

    public HandaliController(BaseController baseController,HandaliService handaliService){
        this.baseController=baseController;
        this.handaliService=handaliService;
    }

    @PostMapping("/handalis")
    public ResponseEntity<HandaliDTO.CreateHandaliResponse> handaliCreate(@RequestHeader("Authorization") String accessToken,
                                                @RequestBody HandaliDTO.CreateHandaliRequest request){

        String token=baseController.extraToken(accessToken);
        Handali handali=handaliService.handaliCreate(token,request.getNickname());
        HandaliDTO.CreateHandaliResponse createHandaliResponse=new HandaliDTO.CreateHandaliResponse(handali.getHandaliId(),handali.getNickname(),handali.getStartDate(),"한달이가 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(createHandaliResponse);
    }

    // [한달이 상태 조회]
    @GetMapping("/handalis/{handali_id}")
    public ResponseEntity<HandaliDTO.HandaliStatusResponse> getHandaliStatus(
            @PathVariable("handali_id") Long handaliId,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByIdAndMonth(handaliId, token);

        return ResponseEntity.ok(response);
    }

    // [스탯 조회]
    @GetMapping("/handalis/{handali_id}/stats")
    public ResponseEntity<HandaliDTO.StatResponse> getHandaliStats(
            @PathVariable("handali_id") Long handaliId,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);

        // Service 호출
        HandaliDTO.StatResponse response = handaliService.getStats(handaliId, token);

        // 응답 반환
        return ResponseEntity.ok(response);
    }


}
