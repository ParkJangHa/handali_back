package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.JobStatDTO;
import com.handalsali.handali.service.ApartmentService;
import com.handalsali.handali.service.HandaliService;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final BaseController baseController;

    public ApartmentController(ApartmentService apartmentService, BaseController baseController) {
        this.apartmentService = apartmentService;
        this.baseController = baseController;
    }

    //[아파트 내 모든 한달이 조회]
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllApartments(
            @RequestHeader("Authorization") String accessToken) { // JWT 인증 필수

        // 토큰에서 유저 정보 추출
        String token = baseController.extraToken(accessToken);

        // 모든 한달이 조회
        List<JobStatDTO.JobResponse> handalis = apartmentService.getAllHandalis();

        if (handalis.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "등록된 한달이가 없습니다."));
        }

        // JSON 형식으로 {"apartments": [...]} 반환
        Map<String, Object> response = new HashMap<>();
        response.put("apartments", handalis);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
