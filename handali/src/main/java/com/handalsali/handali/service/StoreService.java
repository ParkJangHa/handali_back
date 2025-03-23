package com.handalsali.handali.service;

import com.handalsali.handali.domain.StoreItem;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.StoreItemRepository;
import com.handalsali.handali.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final UserService userService;
    private final StoreItemRepository storeItemRepository;
    private final UserRepository userRepository;

    // 특정 카테고리 내 특정 이름의 아이템 조회
    public StoreItem getItemByCategoryAndName(String category, String name) {
        return storeItemRepository.findByCategory(category)
                .stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    // 카테고리별 아이템 조회
    public List<StoreItem> getItemsByCategory(String category,String token) {
        return storeItemRepository.findByCategory(category);
    }

    // 사용자 코인 잔액 조회
    public int getUserCoins(String token) {
        User user = userService.tokenToUser(token);
        return user.getTotal_coin();
    }

    // 아이템 구매 처리
    @Transactional
    public String buyItem(String category, String name, int price, String token) {
        // 유저 조회
        User user = userService.tokenToUser(token);

        // 유저의 코인이 부족한지 확인
        if (user.getTotal_coin() < price) {
            return "코인이 부족합니다.";
        }

        //아이템 조회
        Optional<StoreItem> itemOpt = storeItemRepository.findByCategoryAndName(category, name);

        if (itemOpt.isEmpty()) {
            return "아이템을 찾을 수 없습니다.";
        }

        StoreItem item = itemOpt.get();

        // 이미 구매한 아이템인지 확인
        if (item.isBuy()) {
            return "이미 구매한 아이템입니다.";
        }

        // 코인 차감 후 저장
        user.setTotalCoin(user.getTotal_coin() - price);
        userRepository.save(user); // 변경된 코인 잔액 저장

        // 아이템 구매 처리
        item.setBuy(true);
        storeItemRepository.save(item);

        return "아이템을 구매했습니다.";
    }
}
