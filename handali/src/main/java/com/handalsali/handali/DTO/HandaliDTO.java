package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class HandaliDTO {
    @Data
    @AllArgsConstructor
    public static class CreateHandaliResponse{
        private long handali_id;
        private String nickaname;
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
    public static class ApartEnterResponse {
        private int apart_id;
        private int floor;
    }

    @Data
    @AllArgsConstructor
    public static class HandaliInApartmentResponse {
        private int apart_id;
        private int floor;
        private String nickname;
        private LocalDate start_date;
        private String job_name;
        private int salary;
        private String type_name;
        private float value;
    }

}
