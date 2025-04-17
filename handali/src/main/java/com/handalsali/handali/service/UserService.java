package com.handalsali.handali.service;

import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.EmailAlreadyExistsException;
import com.handalsali.handali.exception.UserNotFoundException;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    /**[회원가입]*/
    public User signUp(String name, String email, String password, String phone, LocalDate birthday){
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException();
        }
        String encryptedPassword = passwordEncoder.encode(password);
        User user = new User(email,name,encryptedPassword,phone,birthday);
        userRepository.save(user);

        return  user;
    }

    /**토큰으로 사용자 찾기*/
    public User tokenToUser(String accessToken) {
        long userId=jwtUtil.extractUserId(accessToken);
        return userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);
    }

}
