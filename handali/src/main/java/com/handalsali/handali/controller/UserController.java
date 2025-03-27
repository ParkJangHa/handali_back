package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.UserDTO;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    private final BaseController baseController;
    public UserController(UserService userService, BaseController baseController) {
        this.userService = userService;
        this.baseController = baseController;
    }

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "새로운 사용자 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "name": "minsu"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "중복된 이메일",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "message": "중복된 이메일 입니다."
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "필수 입력값 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "birthday": "생년월일을 입력해주세요.",
                      "password": "비밀번호를 입력해주세요.",
                      "phone": "전화번호를 입력해주세요.",
                      "name": "이름을 입력해주세요.",
                      "email": "이메일을 입력해주세요."
                    }
                    """)))
    })
    @PostMapping("/signup")
    public ResponseEntity<UserDTO.SignUpResponse> signUp(@Validated @RequestBody UserDTO.SignUpRequest request){
        User user = userService.signUp(request.getName(), request.getEmail(), request.getPassword(), request.getPhone(), request.getBirthday());
        UserDTO.SignUpResponse response = new UserDTO.SignUpResponse(user.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "사용자가 회원가입 후 로그인 시도")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "Bearer": "eyJ..."
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "message": "이메일 또는 비밀번호가 틀렸습니다."
                    }
                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody UserDTO.LogInRequest request){
        String accessToken=userService.logIn(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "로그아웃 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "message": "로그아웃 되었습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "유효하지 않은 토큰입니다."
                    }
                    """)))
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@RequestHeader("Authorization") String accessToken) {
        String token = baseController.extraToken(accessToken);
        userService.logOut(token);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}
