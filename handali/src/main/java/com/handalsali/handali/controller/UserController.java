package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.UserDTO;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    private BaseController baseController;
    public UserController(UserService userService, BaseController baseController) {
        this.userService = userService;
        this.baseController = baseController;
    }

    /**회원가입*/
    @PostMapping("/signup")
    public ResponseEntity<UserDTO.SignUpResponse> signUp(@Validated @RequestBody UserDTO.SignUpRequest request){
        User user = userService.signUp(request.getName(), request.getEmail(), request.getPassword(), request.getPhone(), request.getBirthday());
        UserDTO.SignUpResponse response = new UserDTO.SignUpResponse(user.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**로그인*/
    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody UserDTO.LogInRequest request){
        String accessToken=userService.logIn(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    /**로그아웃*/
    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@RequestHeader("Authorization") String accessToken) {
        String token = baseController.extraToken(accessToken);
        userService.logOut(token);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}
