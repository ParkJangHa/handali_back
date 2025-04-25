package com.handalsali.handali.DTO;

import com.handalsali.handali.enums.Categoryname;
import com.handalsali.handali.enums.CreatedType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;


public class HabitDTO {

    /**습관 추가 최종 응답 dto*/
    @Data
    @AllArgsConstructor
    public static class AddHabitApiResponse{
        private String message;
    }

    /**습관 추가 요청 dto*/
    @Data
    @AllArgsConstructor
    public static class AddHabitRequest{
        @Schema(description = "카테고리 종류", example = "ART")
        @NotBlank(message = "카테고리를 입력해주세요.")
        private Categoryname category;
        @Schema(description = "세부습관", example = "서예")
        @NotBlank(message = "세부습관을 입력해주세요.")
        private String details;
        @Schema(description = "생성자 타입", example = "USER")
        @NotBlank(message = "생성자를 입력해주세요.")
        private CreatedType created_type;
    }

    /**습관 추가 요청 최종 dto*/
    @Data
    @AllArgsConstructor
    public static class AddHabitApiRequest{
        private List<AddHabitRequest> habits;
    }

    /** 카테고리별 습관 조회에 대한 개별 습관 DTO*/
    @Data
    @AllArgsConstructor
    public static class HabitByCategoryResponse {
        @Schema(description = "습관 id", example = "3")
        @NotBlank(message = "습관 id.")
        private Long habitId;
        @Schema(description = "습관 이름", example = "노래 만들기")
        @NotBlank(message = "습관 이름")
        private String detailedHabitName;
    }

    /** 카테고리별 습관 조회에 대한 응답 DTO*/
    @Data
    @AllArgsConstructor
    public static class HabitsByCategoryResponse {
        private Categoryname category;
        private int month;
        private List<HabitByCategoryResponse> habits;
    }

    /** 개발자와 사용자가 설정한 습관 조회에 대한 응답 DTO*/
    @Data
    @AllArgsConstructor
    public static class DeveloperHabitResponse {
        @Schema(description = "카테고리 종류", example = "ACTIVITY")
        private String category;
        private List<HabitDetail> habits;

        /** 개발자 습관 조회의 개별 습관 세부 DTO*/
        @Data
        @AllArgsConstructor
        public static class HabitDetail {
            private Long id;
            @Schema(description = "카테고리에 따른 세부 활동명", example = "서핑")
            private String detail;
        }
    }

    @Data
    public static class getHabitsApiResponse{
        @Schema(description = "카테고리 종류", example = "ART")
        private String category;
        private List<getHabitResponse> habits;
    }
    @Data
    public static class getHabitResponse{
        @Schema(description = "카테고리에 따른 세부 활동명", example = "노래 만들기")
        private String detail;
    }
}
