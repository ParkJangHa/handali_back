package com.handalsali.handali.exception;

public class HabitNotExistsException extends RuntimeException{
    public HabitNotExistsException(String message) {
        super(message);
    }
    public HabitNotExistsException() {}
}
