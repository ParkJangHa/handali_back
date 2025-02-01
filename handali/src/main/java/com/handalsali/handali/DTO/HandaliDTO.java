package com.handalsali.handali.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApartmentResponse {
        private int apart_id;
        private int floor;
    }
}
