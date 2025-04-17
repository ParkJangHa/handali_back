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
    /**이메일 중복 불가*/
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> EmailAlreadyExistsException(Exception e){
        ErrorResponse errorResponse=new ErrorResponse("중복된 이메일 입니다.",HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**입력값 유효성 검사*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**사용자 유무 확인*/
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundException(Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당하는 사용자가 없습니다.");
    }

    /**입력 데이터 받을 때, 빈칸 불가*/
    @ExceptionHandler(MoreOneLessThreeSelectException.class)
    public ResponseEntity<String> noBlankException(MoreOneLessThreeSelectException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    /**하루에 같은 습관은 하나만 기록 가능*/
    @ExceptionHandler(TodayHabitAlreadyRecordException.class)
    public ResponseEntity<String> todayHabitAlreadyRecordException(TodayHabitAlreadyRecordException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    /**카테고리와 세부습관에 해당하는 습관이 없을 때*/
    @ExceptionHandler(HabitNotExistsException.class)
    public ResponseEntity<String> habitNotExistsException(HabitNotExistsException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

    }
    /**한달에 한달이 한마리 초과 불가*/
    @ExceptionHandler(HanCreationLimitException.class)
    public ResponseEntity<String> hanCreationLimitException(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("한달에 한 마리만 생성 가능합니다.");
        }
    /**카테고리명, 생성자명이 enum에 정의해둔 값과 다를 때*/
    @ExceptionHandler(CreatedTypeOrCategoryNameWrongException.class)
    public ResponseEntity<String> createdTypeOrCategoryNameWrongException(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("카테고리명/생성자명이 옳지 않습니다.");
    }

    /**이번달의 한달이를 찾을 수 없음*/
    @ExceptionHandler(HandaliNotFoundException.class)
    public ResponseEntity<String> handaliNotFoundException(HandaliNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**한달이의 스탯을 찾을 수 없음*/
    @ExceptionHandler(HandaliStatNotFoundException.class)
    public ResponseEntity<String> handaliStatNotFoundException(HandaliStatNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /** 잘못된 카테고리 or 상품명으로 구매 요청 시*/
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /** 이미 구매한 상품을 다시 구매 요청 시*/
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /** 알 수 없는 오류 발생 시*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
    }

    /**상점에 아이템이 존재하지 않는 오류*/
    @ExceptionHandler(StoreItemNotExistsException.class)
    public ResponseEntity<String> storeCategoryNotExsitsException(StoreItemNotExistsException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**상점에서 이미 구매한 물건인 경우 오류*/
    @ExceptionHandler(StoreItemAlreadyBoughtException.class)
    public ResponseEntity<String> storeCategoryAlreadyBoughtException(StoreItemAlreadyBoughtException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**상점에서 코인 부족 오류*/
    @ExceptionHandler(InsufficientCoinException.class)
    public ResponseEntity<String> insufficientCoinException(InsufficientCoinException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
