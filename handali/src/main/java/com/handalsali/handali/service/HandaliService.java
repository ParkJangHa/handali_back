package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.HandaliRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class HandaliService {
    private UserService userService;
    private HandaliRepository handaliRepository;

    public HandaliService(UserService userService, HandaliRepository handaliRepository) {
        this.userService = userService;
        this.handaliRepository = handaliRepository;
    }

    //한달이 생성
    public Handali handaliCreate(String token,String nickname){
        User user=userService.tokenToUser(token);
        return new Handali(nickname, LocalDate.now(),user);
    }


}
