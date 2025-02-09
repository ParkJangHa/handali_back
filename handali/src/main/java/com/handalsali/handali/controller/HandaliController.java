package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.service.HandaliService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/handalis")
public class HandaliController {
    private BaseController baseController;
    private HandaliService handaliService;

    public HandaliController(BaseController baseController,HandaliService handaliService){
        this.baseController=baseController;
        this.handaliService=handaliService;
    }

    //[한달이 생성].
    @PostMapping
    public ResponseEntity<HandaliDTO.CreateHandaliResponse> handaliCreate(@RequestHeader("Authorization") String accessToken,
                                                @RequestBody HandaliDTO.CreateHandaliRequest request){

        String token=baseController.extraToken(accessToken);
        Handali handali=handaliService.handaliCreate(token,request.getNickname());
        HandaliDTO.CreateHandaliResponse createHandaliResponse=new HandaliDTO.CreateHandaliResponse(handali.getHandaliId(),handali.getNickname(),handali.getStartDate(),"한달이가 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(createHandaliResponse);
    }


    // [한달이 상태 조회]
    @GetMapping("/{handali_id}")
    public ResponseEntity<HandaliDTO.HandaliStatusResponse> getHandaliStatus(
            @PathVariable("handali_id") Long handaliId,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByIdAndMonth(handaliId, token);

        return ResponseEntity.ok(response);
    }

    // [스탯 조회]
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

    // [아파트 입주]
    @PostMapping("/{handali_id}/apartments-enter")
    public ResponseEntity<HandaliDTO.ApartEnterResponse> moveHandaliToApartment(
            @PathVariable("handali_id") Long handaliId,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        HandaliDTO.ApartEnterResponse response = handaliService.moveHandaliToApartment(handaliId, token);

        return ResponseEntity.ok(response);
    }
}
