package com.handalsali.handali.service;

import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.EmailOrPwNotCorrectException;
import com.handalsali.handali.exception.EmailAlreadyExistsException;
import com.handalsali.handali.exception.TokenValidationException;
import com.handalsali.handali.exception.UserNotFoundException;
import com.handalsali.handali.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    /**[회원가입]*/
    public User signUp(String name, String email, String password, String phone, LocalDate birthday){
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException();
        }
        User user = new User(email,name,password,phone,birthday);
        userRepository.save(user);

        return  user;
    }

    /**[로그인]*/
    public String logIn(String email,String password){
        User user= userRepository.findByEmail(email);

        if(user==null||!user.checkPassword(password))
            throw new EmailOrPwNotCorrectException();

        // Access Token 및 Refresh Token 생성
        String accessToken = jwtUtil.generateToken(email,user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // Refresh Token 저장
        refreshTokenService.saveRefreshToken(email, refreshToken, 7 * 24 * 60 * 60 * 1000); // 7일 만료
        return "Bearer "+accessToken;

        // (액세스, 리프레시 토큰 함께 반환)클라이언트에게 반환할 데이터 구조 변경
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("accessToken", "Bearer " + accessToken);
//        tokens.put("refreshToken", refreshToken);
//        return tokens;
    }

    /**[로그아웃]*/
    public void logOut(String accessToken) {
        String email = tokenToEmail(accessToken);
        refreshTokenService.deleteRefreshToken(email);
        
    }

    /**[토큰 갱신]*/
    public String refreshTokenToAccessToken(String refreshToken){
        // 리프레시 토큰 검증
        Claims claims = jwtUtil.validateToken(refreshToken);
        String email = claims.getSubject();

        // 저장된 리프레시 토큰과 비교하여 유효한지 확인
        if (!refreshTokenService.isValidRefreshToken(email, refreshToken)) {
            throw new TokenValidationException("Invalid refresh token");
        }

        // 새로운 액세스 토큰 발급
        User user = userRepository.findByEmail(email);
        return "Bearer " + jwtUtil.generateToken(email, user.getUserId());
    }

    /**토큰으로 이메일 찾기*/
    public String tokenToEmail(String accessToken){
        // 토큰에서 이메일 추출
        return jwtUtil.validateToken(accessToken).getSubject();
    }

    /**아이디로 사용자 찾기*/
    public User userIdToUser(long userId){
        User user= userRepository.findByUserId(userId).orElseThrow(()-> new UserNotFoundException());
        return user;
    }

    /**토큰으로 사용자 찾기*/
    public User tokenToUser(String accessToken) {
        long userId=jwtUtil.extractUserId(accessToken);
        return userIdToUser(userId);
    }

    /** 유저 정보 저장*/
    public void save(User user) {
        userRepository.save(user);
    }
}
