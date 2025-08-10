package com.handalsali.handali.service;

import com.handalsali.handali.domain.Store;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserItem;
import com.handalsali.handali.domain.UserStore;
import com.handalsali.handali.exception.EmailAlreadyExistsException;
import com.handalsali.handali.exception.UserNotFoundException;
import com.handalsali.handali.repository.StoreRepository;
import com.handalsali.handali.repository.UserItemRepository;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.repository.UserStoreRepository;
import com.handalsali.handali.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    private final UserStoreRepository userStoreRepository;
    private final UserItemRepository userItemRepository;


    /**[회원가입]*/
    public User signUp(String name, String email, String password, String phone, LocalDate birthday){
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException();
        }
        String encryptedPassword = passwordEncoder.encode(password);
        User user = new User(email,name,encryptedPassword,phone,birthday);
        userRepository.save(user);

        //기본 배경, 소파, 벽장식, 바닥장식 추가
        List<UserStore>userStores=new ArrayList<>();
        List<UserItem>userItems=new ArrayList<>();

        List<Store> storeItems = storeRepository.findByPrice(0);
        for(Store store : storeItems){
            userStores.add(new UserStore(user, store)); //구매
            userItems.add(new UserItem(user,store)); //아이템 추가
        }

        userStoreRepository.saveAll(userStores);
        userItemRepository.saveAll(userItems);

        return  user;
    }

    /**토큰으로 사용자 찾기*/
    public User tokenToUser(String accessToken) {
        long userId=jwtUtil.extractUserId(accessToken);
        return userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);
    }

    public void giveQuestReward(User user, int coin) {
        user.setTotal_coin(user.getTotal_coin() + coin);
        userRepository.save(user);
    }

}
