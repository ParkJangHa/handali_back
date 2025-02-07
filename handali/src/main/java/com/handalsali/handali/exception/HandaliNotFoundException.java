package com.handalsali.handali.exception;

public class HandaliNotFoundException extends RuntimeException{
    public HandaliNotFoundException() {
        super("해당 한달이를 찾을 수 없습니다.");
    }

    public HandaliNotFoundException(String message) {
        super(message);
    }
    public HandaliNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public HandaliNotFoundException(Throwable cause) {
        super("해당 한달이를 찾을 수 없습니다.", cause);
    }
}
