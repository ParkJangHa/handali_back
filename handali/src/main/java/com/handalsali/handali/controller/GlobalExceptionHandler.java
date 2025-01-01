package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.ErrorResponse;
import com.handalsali.handali.exception.EmailAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//애플리케이션 전반에서 발생하는 예외를 하나의 클래스에서 처리할 수 있도록
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> EmailAlreadyExistsException(Exception e){
        ErrorResponse errorResponse=new ErrorResponse("중복된 이메일 입니다.",HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
