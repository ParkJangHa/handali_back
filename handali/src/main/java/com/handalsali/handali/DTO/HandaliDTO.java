package com.handalsali.handali.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.handalsali.handali.domain.Handali;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class HandaliDTO {
    @Data
    @AllArgsConstructor
    public static class CreateHandaliResponse{
        private long handali_id;
        private String nickname;
        private LocalDate start_date;
        private String message;
    }

    @Data
    public static class CreateHandaliRequest{
        @Schema(description = "생성한 한달이의 닉네임", example = "handa")
        private String nickname;
    }

    /**[한달이 상태 조회] 응답*/
    @Data
    @AllArgsConstructor
    public static class HandaliStatusResponse {
        @Schema(description = "해당하는 한달이의 닉네임", example = "handa")
        private String nickname;
        @Schema(description = "한달이가 생성된 일수", example = "3")
        private int days_since_created;
        @Schema(description = "토탈 코인", example = "100")
        private int total_coin;
        @Schema(description = "변화한 한달이 사진", example = "image_0_0_0.png")
        private String handali_img;
        private String background_img;
        private String wall_img;
        private String sofa_img;
        private String floor_img;

        private float activity_value;
        private float art_value;
        private float intelligence_value;

        private int max_stat_activity;
        private int max_stat_art;
        private int max_stat_intelligence;
    }


    @Data
    @AllArgsConstructor
    public static class StatResponse {
        private List<StatDetailDTO> stat;
    }

    @Data
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentHandaliResponse {
        @Schema(description = "마지막 한달이 닉네임", example = "handa")
        private String nickname;
        @Schema(description = "마지막 한달이 아이디", example = "8")
        private Long handali_id;
        @Schema(description = "마지막 한달이 시작 날짜", example = "2025-03-07")
        private LocalDate start_date;
        @Schema(description = "마지막 한달이 직업", example = "백수")
        private String job_name;
        @Schema(description = "마지막 한달이 주급", example = "10")
        private int salary;
        @Schema(description = "마지막 한달이 이미지", example = "image_0_0_0.png")
        private String image;
    }

    @Data
    @AllArgsConstructor
    public static class GetWeekSalaryResponseDTO {
        private String nickname;
        private int salary;
        private LocalDate start_date;
    }

    @Data
    @AllArgsConstructor
    public static class GetWeekSalaryApiResponseDTO {
        private List<GetWeekSalaryResponseDTO> handalis_salary;
    }
}
