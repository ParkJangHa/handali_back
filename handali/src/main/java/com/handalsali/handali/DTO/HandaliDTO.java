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

    /**[한달이 상태 조회] 응답*/
    @Data
    @AllArgsConstructor
    public static class HandaliStatusResponse {
        private String nickname;
        private int days_since_created;
        private int total_coin;
    }


    @Data
    @AllArgsConstructor
    public static class StatResponse {
        private List<StatDetailDTO> stat;
    }

}
