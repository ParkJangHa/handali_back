package com.handalsali.handali.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class HandaliNotFoundException extends RuntimeException{
    public HandaliNotFoundException(String message) {
        super(message);
    }
}
