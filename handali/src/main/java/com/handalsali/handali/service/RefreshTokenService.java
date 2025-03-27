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

    /** Refresh Token 저장*/
    public void saveRefreshToken(String email, String refreshToken, long expirationTime) {
        redisTemplate.opsForValue().set("refreshToken:" + email, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    /** Refresh Token 조회 (유효성 검증 포함) */
    public boolean isValidRefreshToken(String email, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("refreshToken:" + email);

        // 토큰이 존재하지 않거나 일치하지 않으면 유효하지 않음
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            return false;
        }

        // 만료 시간이 남아 있는지 확인
        Long expiration = redisTemplate.getExpire("refreshToken:" + email, TimeUnit.MILLISECONDS);
        if (expiration == null || expiration <= 0) {
            return false;
        }

        return true;
    }

    /** Refresh Token의 남은 만료 시간 조회 */
    public long getRefreshTokenExpiration(String email) {
        Long expiration = redisTemplate.getExpire("refreshToken:" + email, TimeUnit.MILLISECONDS);
        return (expiration != null) ? expiration : -1;
    }

    /** Refresh Token 삭제*/
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("refreshToken:" + email);
    }

}
