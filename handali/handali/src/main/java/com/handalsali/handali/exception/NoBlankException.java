package com.handalsali.handali.exception;

public class NoBlankException extends RuntimeException{
    public NoBlankException(String message){super(message);}
    public NoBlankException(String message, Throwable cause) {
        super(message, cause);
    }
}
