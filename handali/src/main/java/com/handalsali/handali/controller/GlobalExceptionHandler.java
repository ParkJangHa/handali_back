package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.ErrorResponse;
import com.handalsali.handali.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//애플리케이션 전반에서 발생하는 예외를 하나의 클래스에서 처리할 수 있도록
@RestControllerAdvice
public class GlobalExceptionHandler {
    //이메일 중복 불가
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> EmailAlreadyExistsException(Exception e){
        ErrorResponse errorResponse=new ErrorResponse("중복된 이메일 입니다.",HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    //입력값 유효성 검사
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    //이메일/비밀번호 틀림
    @ExceptionHandler(EmailOrPwNotCorrectException.class)
    public ResponseEntity<String> badCredentialsException(Exception e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 틀렸습니다.");
    }

    //jwt 토큰 유효성 검사
    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<String> tokenValidationException(TokenValidationException e){
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
    }

    //사용자 유무 확인
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundException(Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당하는 사용자가 없습니다.");
    }

    //입력 데이터 받을 때, 빈칸 불가
    @ExceptionHandler(NoBlankException.class)
    public ResponseEntity<String> noBlankException(NoBlankException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    //하루에 같은 습관은 하나만 기록 가능
    @ExceptionHandler(TodayHabitAlreadyRecordException.class)
    public ResponseEntity<String> todayHabitAlreadyRecordException(TodayHabitAlreadyRecordException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
