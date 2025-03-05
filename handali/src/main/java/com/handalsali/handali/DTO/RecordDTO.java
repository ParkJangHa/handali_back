package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.Categoryname;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

public class RecordDTO {
    @Data
    public static class recordTodayHabitRequest{
        private Categoryname category;
        private String detailed_habit_name;
        private float time;
        private int satisfaction;
        private LocalDate date;

    }
    @Data
    @AllArgsConstructor
    public static class recordTodayHabitResponse{
        private long record_id;
        private String message;
        private boolean appearance_change;
    }
}
