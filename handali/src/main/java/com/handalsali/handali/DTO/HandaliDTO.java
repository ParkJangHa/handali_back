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
        private Long handali_Id;         // 한달이 ID
        private String nickname;        // 닉네임
        private int days_Since_Created;   // 생성 이후 경과 일수
        private String message;         // 30일이 되면, 메시지 추가
    }


    @Data
    @AllArgsConstructor
    public static class StatResponse {
        private List<StatDetail> stat;
    }

}

