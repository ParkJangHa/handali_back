package com.handalsali.handali.DTO;

import com.handalsali.handali.DTO.Record.MonthlyRecordCountResponse;
import com.handalsali.handali.DTO.Record.SatisfactionAvgByCategoryResponse;
import com.handalsali.handali.DTO.Record.TotalRecordsByCategoryResponse;
import com.handalsali.handali.DTO.Record.TotalTimeByCategoryResponse;
import com.handalsali.handali.enums.Categoryname;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class RecordDTO {
    @Data
    @AllArgsConstructor
    public static class RecordTodayHabitRequest {
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
    public static class RecordTodayHabitResponse {
        private long record_id;
        private String message;
        private boolean appearance_change;
    }

    @Data
    @AllArgsConstructor
    public static class RecordSummaryResponse{
        private List<SatisfactionAvgByCategoryResponse> satisfaction_avg_by_category_month; //카테고리별 만족도 평균
        private List<TotalTimeByCategoryResponse> total_time_by_category_month; //카테고리별 시간 총합
        private int total_records_month; //이번달 총 기록 횟수
        private List<TotalRecordsByCategoryResponse> total_records_by_category_month; //카테고리별 총 기록 횟수
        private List<MonthlyRecordCountResponse> monthly_record_count; //달별 기록 횟수

        List<SatisfactionAvgByCategoryResponse> satisfaction_avg_by_category_week;
        List<TotalTimeByCategoryResponse> total_time_by_category_week;
        int total_records_week;
        List<TotalRecordsByCategoryResponse> total_records_by_category_week;
    }

//    @Data
//    @AllArgsConstructor
//    public static class SatisfactionAvgByCategoryResponse {
//        private Categoryname category;
//        private Double avg_satisfaction;
//    }

//    @Data
//    @AllArgsConstructor
//    public static class TotalTimeByCategoryResponse {
//        private Categoryname category;
//        private double total_time;
//    }

//    @Data
//    @AllArgsConstructor
//    public static class TotalRecordsByCategoryResponse {
//        private Categoryname category;
//        private long total_records;
//    }

//    @Data
//    @AllArgsConstructor
//    public static class MonthlyRecordCountResponse {
//        private int year;
//        private int month;
//        private long totalRecords;
//    }
}
