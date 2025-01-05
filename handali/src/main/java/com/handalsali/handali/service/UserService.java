package com.handalsali.handali.service;

import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.EmailOrPwNotCorrectException;
import com.handalsali.handali.exception.EmailAlreadyExistsException;
import com.handalsali.handali.exception.TokenValidationException;
import com.handalsali.handali.repository.UserRepositoryInterface;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class UserService {
    private final UserRepositoryInterface userRepositoryInterface;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepositoryInterface userRepositoryInterface, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepositoryInterface = userRepositoryInterface;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    //feat: 회원가입
    public User signUp(String name, String email, String password, String phone, Date birthday){
        if(userRepositoryInterface.existsByEmail(email)){
            throw new EmailAlreadyExistsException();
        }
        User user = new User(email,name,password,phone,birthday);
        userRepositoryInterface.save(user);

        return  user;
    }

    //feat: 로그인
    public String logIn(String email,String password){
        User user=userRepositoryInterface.findByEmail(email);
        if(user==null || !user.checkPassword(password))
            throw new EmailOrPwNotCorrectException();

        // Access Token 및 Refresh Token 생성
        String accessToken = jwtUtil.generateToken(email,user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // Refresh Token 저장
        refreshTokenService.saveRefreshToken(email, refreshToken, 7 * 24 * 60 * 60 * 1000); // 7일 만료
        return "Bearer "+accessToken;
    }

    //feat: 로그아웃
    public void logOut(String accessToken) {
        String email = tokenToEmail(accessToken);
        refreshTokenService.deleteRefreshToken(email);
        
    }

    //토큰으로 이메일 찾기
    public String tokenToEmail(String accessToken){
        // 토큰에서 이메일 추출
        return jwtUtil.validateToken(accessToken).getSubject();
    }

    //토큰으로 사용자 아이디 찾기
    public long tokenToUserId(String accessToken){
        return jwtUtil.extractUserId(accessToken);
    }
}
