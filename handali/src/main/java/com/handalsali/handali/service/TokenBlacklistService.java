package com.handalsali.handali.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final StringRedisTemplate redisTemplate;

    /**
     * 토큰을 블랙리스트에 저장
     */
    public void blacklistToken(String token,long expirationMillis){
        redisTemplate.opsForValue().set(token,"blacklisted",expirationMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 해당 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isTokenBlacklisted(String token){
        return redisTemplate.hasKey(token);
    }
}
