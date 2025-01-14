package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;


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
}
