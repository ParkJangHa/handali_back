package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HandaliDTO.ApartmentResponse;
import com.handalsali.handali.service.HandaliService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartments")
public class ApartmentController {
    private final HandaliService handaliService;
    private final BaseController baseController;

    public ApartmentController(HandaliService handaliService, BaseController baseController) {
        this.handaliService = handaliService;
        this.baseController = baseController;
    }

    //사용자가 배정된 아파트 정보 조회
    @GetMapping
    public ResponseEntity<List<ApartmentResponse>> getUserApartments(
            @RequestHeader("Authorization") String accessToken) {
        String token = baseController.extraToken(accessToken);
        List<ApartmentResponse> response = handaliService.getUserApartments(token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}