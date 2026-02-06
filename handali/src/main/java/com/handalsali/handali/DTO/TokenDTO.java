package com.handalsali.handali.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class TokenDTO {
    @Getter
    @AllArgsConstructor
    public static class TokenResponse{
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}
