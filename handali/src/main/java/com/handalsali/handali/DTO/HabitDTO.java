package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


public class HabitDTO {
    @Data
    @AllArgsConstructor
    public static class AddHabitResponse{
        private Categoryname category;
        private String details;
        private CreatedType created_type;
    }

    @Data
    @AllArgsConstructor
    public static class AddHabitApiResponse{
        private String message;
        private AddHabitResponse habits;
    }

    @Data
    public static class AddHabitRequest{
        @NotBlank(message = "카테고리를 입력해주세요.")
        private Categoryname category;
        @NotBlank(message = "세부습관을 입력해주세요.")
        private String details;
        @NotBlank(message = "생성자를 입력해주세요.")
        private CreatedType created_type;

    }

    // 개발자와 사용자가 설정한 습관 조회에 대한 응답 DTO
    @Data
    @AllArgsConstructor
    public static class DeveloperHabitResponse {
        private String category;                      // 카테고리 이름
        private List<HabitDetail> habits;           // 카테고리 내 습관 정보 리스트

        // 개발자 습관 조회의 개별 습관 세부 DTO
        @Data
        @AllArgsConstructor
        public static class HabitDetail {
            private Long id;                         // 습관의 고유 ID
            private String detail;             // 습관의 상세 이름
        }
    }

    // 카테고리별 습관 조회에 대한 개별 습관 DTO
    @Data
    @AllArgsConstructor
    public static class HabitByCategoryResponse {
        private Long habit_id;              // 습관의 고유 ID
        private String detailed_Habit_Name;  // 습관의 상세 이름
    }

    // 카테고리별 습관 조회에 대한 응답 DTO
    @Data
    @AllArgsConstructor
    public static class HabitsByCategoryResponse {
        private Categoryname category;                       // 카테고리
        private int month;                                   // 조회된 월
        private List<HabitByCategoryResponse> habits;        // 습관 리스트
    }
}
