package com.handalsali.handali.DTO;

import com.handalsali.handali.enums.Categoryname;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

public class RecordDTO {
    @Data
    @AllArgsConstructor
    public static class recordTodayHabitRequest{
        @Schema(description = "카테고리 명", example = "ACTIVITY")
        private Categoryname category;
        @Schema(description = "세부 습관 명", example = "헬스장 가기")
        private String detailed_habit_name;
        @Schema(description = "사용자가 입력한 습관 시간", example = "3.5")
        private float time;
        @Schema(description = "사용자가 입력한 습관 만족도", example = "9")
        private int satisfaction;
        @Schema(description = "사용자가 입력한 날짜", example = "2024-11-02")
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
