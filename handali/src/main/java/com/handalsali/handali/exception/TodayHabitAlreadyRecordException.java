package com.handalsali.handali.exception;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

public class TodayHabitAlreadyRecordException extends RuntimeException{
    public TodayHabitAlreadyRecordException(String format){
        super(format);
    }
}
