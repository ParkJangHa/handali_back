package com.handalsali.handali.DTO;

import com.handalsali.handali.domain.Handali;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        private String image;
    }


    @Data
    @AllArgsConstructor
    public static class StatResponse {
        private List<StatDetailDTO> stat;
    }

    @Data
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
    public static class HandaliInApartmentResponse {
        private Long apart_id;
        private int floor;
        private String nickname;
        private LocalDate start_date;
        private String job_name;
        private int week_salary;
        private String type_name;
        private float stat_value;

//        public static HandaliInApartmentResponse fromEntity(Handali handali) {
//            if (handali.getApart() == null) {
//                throw new IllegalStateException("Handali가 아파트 정보를 가지고 있지 않습니다.");
//            }
//
//            System.out.println("DEBUG: " + handali.getNickname() + "의 아파트 ID: " + handali.getApart().getApartId());
//            System.out.println("DEBUG: " + handali.getNickname() + "의 층수: " + handali.getApart().getFloor());
//
//            return new HandaliInApartmentResponse(
//                    Long.valueOf(handali.getApart().getApartId().getApartId()),  // 아파트 ID
//                    handali.getApart().getFloor(),  // 층 수
//                    handali.getNickname(),  // 닉네임
//                    handali.getStartDate(),  // 생성일
//                    handali.getJob() != null ? handali.getJob().getName() : null,  // 직업명
//                    handali.getJob() != null ? handali.getJob().getWeekSalary() : 0,  // 주급
//                    "체력",  // 예제 스탯 이름
//                    30.5f   // 예제 스탯 값
//            );
//        }
    }
}
