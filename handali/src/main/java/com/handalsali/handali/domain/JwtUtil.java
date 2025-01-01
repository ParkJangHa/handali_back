package com.handalsali.handali.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class JwtUtil {
    private static final String SECRET_KEY = "ef593a384decc4d9c06a480f166538afa52ec9fa3b9d90b23aad66595bafbea7";

    public static String generateToken(String username) {
        long expirationTime = 1000 * 60 * 60; // 1시간

        return Jwts.builder()
                .setSubject(username) // 사용자 정보
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 서명 알고리즘과 키
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody(); // 토큰의 Payload 반환
    }
}
