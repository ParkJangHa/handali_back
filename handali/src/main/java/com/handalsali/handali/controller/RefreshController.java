package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.TokenDTO;
import com.handalsali.handali.security.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class RefreshController {
    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenDTO.RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // 1. Refresh Token 검증
            Claims claims = jwtUtil.validateToken(refreshToken);

            // 2. userId, email 추출
            Long userId = claims.get("userId", Long.class);
            String email = claims.getSubject();

            // 3. Redis에서 저장된 Refresh Token 확인
            String savedToken = redisTemplate.opsForValue().get("RT:" + userId);

            if (savedToken == null || !savedToken.equals(refreshToken)) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Refresh token not found or invalid"));
            }

            // 4. 새 Access Token만 발급
            String newAccessToken = jwtUtil.generateAccessToken(email, userId);

            // 5. 기존 Refresh Token 그대로 사용
            TokenDTO.TokenResponse response = new TokenDTO.TokenResponse(
                    newAccessToken,
                    refreshToken
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Token refresh failed: " + e.getMessage()));
        }
    }
}
