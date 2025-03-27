package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HandaliDTO;
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
    public ResponseEntity<List<Map<String,Object>>> getAllHandalisInApartments(
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        List<Map<String,Object>> response=apartmentService.getAllHandalisInApartments(token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
