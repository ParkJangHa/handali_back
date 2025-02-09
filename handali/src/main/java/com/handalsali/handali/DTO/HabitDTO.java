package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;


public class HabitDTO {
    //습관 추가 응답 dto
//    @Data
//    @AllArgsConstructor
//    public static class AddHabitResponse{
//        private Categoryname category;
//        private String details;
//        private CreatedType created_type;
//    }

    //습관 추가 최종 응답 dto
    @Data
    @AllArgsConstructor
    public static class AddHabitApiResponse{
        private String message;
    }

    //습관 추가 요청 dto
    @Data
    public static class AddHabitRequest{
        @NotBlank(message = "카테고리를 입력해주세요.")
        private Categoryname category;
        @NotBlank(message = "세부습관을 입력해주세요.")
        private String details;
        @NotBlank(message = "생성자를 입력해주세요.")
        private CreatedType created_type;
    }

    //습관 추가 요청 최종 dto
    @Data
    public static class AddHabitApiRequest{
        private List<AddHabitRequest> habits;
    }

    // 카테고리별 습관 조회에 대한 개별 습관 DTO
    @Data
    @AllArgsConstructor
    public static class HabitByCategoryResponse {
        private Long habitId;
        private String detailedHabitName;
    }

    // 카테고리별 습관 조회에 대한 응답 DTO
    @Data
    @AllArgsConstructor
    public static class HabitsByCategoryResponse {
        private Categoryname category;
        private int month;
        private List<HabitByCategoryResponse> habits;
    }

    // 개발자와 사용자가 설정한 습관 조회에 대한 응답 DTO
    @Data
    @AllArgsConstructor
    public static class DeveloperHabitResponse {
        private String category;
        private List<HabitDetail> habits;

        // 개발자 습관 조회의 개별 습관 세부 DTO
        @Data
        @AllArgsConstructor
        public static class HabitDetail {
            private Long id;
            private String detail;
        }
    }

    @Data
    public static class getHabitsApiResponse{
        private String category;
        private List<getHabitResponse> habits;
    }
    @Data
    public static class getHabitResponse{
        private String detail;
    }
}
