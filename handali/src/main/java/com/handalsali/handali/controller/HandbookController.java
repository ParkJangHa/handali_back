package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.HandbookDTO;
import com.handalsali.handali.domain.Handbook;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.service.HandaliService;
import com.handalsali.handali.service.HandbookService;
import com.handalsali.handali.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandbookController {
    private final HandbookService handbookService;
    private BaseController baseController;
    private UserService userService;

    public HandbookController(BaseController baseController, HandbookService handbookService,UserService userService){
        this.baseController=baseController;
        this.handbookService = handbookService;
        this.userService=userService;
    }
    @GetMapping("/handbooks")
    public ResponseEntity<HandbookDTO.HandbookApiResponse> getUserHandbooks(@RequestHeader("Authorization") String accessToken) {
        String token=baseController.extraToken(accessToken);
        User user = userService.tokenToUser(token);
        HandbookDTO.HandbookApiResponse apiResponse = handbookService.getUserHandbook(user);
        return ResponseEntity.ok(apiResponse);
    }
}
