package com.handalsali.handali.DTO;

import com.handalsali.handali.domain.Handali;
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
        private String nickname;
    }

    /**[한달이 상태 조회] 응답*/
    @Data
    @AllArgsConstructor
    public static class HandaliStatusResponse {
        private String nickname;
        private int days_since_created;
        private int total_coin;
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
        private String nickname;
        private Long handali_id;
        private LocalDate start_date;
        private String job_name;
        private int salary;
        private String image;
    }
}
