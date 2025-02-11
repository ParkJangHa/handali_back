package com.handalsali.handali.DTO;

import com.handalsali.handali.domain.Handali;
import lombok.AllArgsConstructor;
import lombok.Data;

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
        private String nickname;
    }


    @Data
    @AllArgsConstructor
    public static class HandaliStatusResponse {
        //private Long handali_Id;         // 한달이 ID
        private String nickname;        // 닉네임
        private int days_Since_Created;   // 생성 이후 경과 일수
        private String message;         // 30일이 되면, 메시지 추가
    }


    @Data
    @AllArgsConstructor
    public static class StatResponse {
        private List<StatDetailDTO> stat;
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

        public static HandaliInApartmentResponse fromEntity(Handali handali) {
            if (handali.getApart() == null) {
                throw new IllegalStateException("Handali가 아파트 정보를 가지고 있지 않습니다.");
            }

            System.out.println("DEBUG: " + handali.getNickname() + "의 아파트 ID: " + handali.getApart().getApartId());
            System.out.println("DEBUG: " + handali.getNickname() + "의 층수: " + handali.getApart().getFloor());

            return new HandaliInApartmentResponse(
                    Long.valueOf(handali.getApart().getApartId().getApartId()),  // 아파트 ID
                    handali.getApart().getFloor(),  // 층 수
                    handali.getNickname(),  // 닉네임
                    handali.getStartDate(),  // 생성일
                    handali.getJob() != null ? handali.getJob().getName() : null,  // 직업명
                    handali.getJob() != null ? handali.getJob().getWeekSalary() : 0,  // 주급
                    "체력",  // 예제 스탯 이름
                    30.5f   // 예제 스탯 값
            );
        }
    }
}
