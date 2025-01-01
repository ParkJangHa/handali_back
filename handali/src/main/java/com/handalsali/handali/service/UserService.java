package com.handalsali.handali.service;

import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.EmailAlreadyExistsException;
import com.handalsali.handali.repository.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class UserService {
    @Autowired
    private UserRepositoryInterface userRepositoryInterface;

    //회원가입
    public User signUp(String name, String email, String password, String phone, Date birthday){
        if(userRepositoryInterface.existsByEmail(email)){
            throw new EmailAlreadyExistsException();
        }
        User user = new User(email,name,password,phone,birthday);
        userRepositoryInterface.save(user);

        return  user;
    }

    //로그인
    public void logIn(String email,String password){
        if(!userRepositoryInterface.findByEmail(email))
            return 
    }
}
