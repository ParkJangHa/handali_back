package com.handalsali.handali.service;

import com.handalsali.handali.exception.TokenValidationException;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Data
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    /** Access Token 생성*/
    public String generateToken(String email, long userId) {
        long expirationTime = 1000 * 60 * 60; // 15분 -> 1시간
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new java.util.Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘과 키
                .compact();
    }

    /** Refresh Token 생성*/
    public String generateRefreshToken(String email) {
        long expirationTime = 1000 * 60 * 60 * 24 * 7; // 7일
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**토큰 검증 및 claim 반환*/
    public Claims validateToken(String token) {
        try {
            token = token.trim(); // 공백 제거
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException("Token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new TokenValidationException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            throw new TokenValidationException("Invalid JWT token", e);
        } catch (SignatureException e) {
            throw new TokenValidationException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            throw new TokenValidationException("Token claims string is empty", e);
        }
    }

    /**토큰으로 userId 찾기*/
    public long extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Long.class);
    }
}
