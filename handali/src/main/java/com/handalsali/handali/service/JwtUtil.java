package com.handalsali.handali.service;

import com.handalsali.handali.exception.TokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    public String generateToken(String email) {
        long expirationTime = 1000 * 60 * 60; // 1시간

        return Jwts.builder()
                .setSubject(email) // 사용자 정보
                .setIssuedAt(new java.util.Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘과 키
                .compact();
    }

    public Claims validateToken(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody(); // 토큰의 Payload 반환
        }catch (Exception e){
            throw new TokenValidationException();
        }
    }
}
