package com.handalsali.handali.security;

import com.handalsali.handali.exception.TokenValidationException;
import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;


@Data
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    // ==================== Access Token ====================

    /** Access Token 생성 (15분) */
    public String generateAccessToken(String email, long userId) {
        long expirationTime = 1000L * 60 * 15; // 15분
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("type", "access")  // 타입 구분
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // ==================== Refresh Token ====================

    /** Refresh Token 생성 (7일) */
    public String generateRefreshToken(String email,long userId) {
        long expirationTime = 1000L * 60 * 60 * 24 * 7; // 7일
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("type", "refresh")  // 타입 구분
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // ==================== 공통 메서드 ====================

    /** 토큰 검증 및 claim 반환 */
    public Claims validateToken(String token) {
        try {
            System.out.println("validateToken() 호출됨");
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

    /** 토큰으로 userId 찾기 */
    public long extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Long.class);
    }
}