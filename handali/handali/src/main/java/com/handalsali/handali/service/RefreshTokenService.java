package com.handalsali.handali.service;

import com.handalsali.handali.exception.TokenValidationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token 저장
    public void saveRefreshToken(String email, String refreshToken, long expirationTime) {
        redisTemplate.opsForValue().set("refreshToken:" + email, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get("refreshToken:" + email);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("refreshToken:" + email);
    }

}
