package com.handalsali.handali.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class HandbookDTO {
    @Data
    @AllArgsConstructor
    public static class HandbookResponse {
        private String code;
        private LocalDateTime created_at;
    }

    @Data
    @AllArgsConstructor
    public static class HandbookApiResponse {
        private List<HandbookResponse> handbooks;
    }
}
