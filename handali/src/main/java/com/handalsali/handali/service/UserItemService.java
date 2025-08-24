package com.handalsali.handali.service;

import com.handalsali.handali.DTO.UserItemDTO;
import com.handalsali.handali.domain.*;
import com.handalsali.handali.enums.ItemType;
import com.handalsali.handali.exception.StoreItemNotExistsException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.StoreRepository;
import com.handalsali.handali.repository.UserItemRepository;
import com.handalsali.handali.repository.UserStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserItemService {
    private final UserItemRepository userItemRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;
    private final UserStoreRepository userStoreRepository;
    private final HandaliRepository handaliRepository;

    /**
     * [구매한 물품 적용]
     */
    @Transactional
    public void setUserItem(String token,UserItemDTO.SetUserItemRequest request) {
        //1. 사용자 찾기
        User user = userService.tokenToUser(token);

        //2. 이미 적용한 물품이 있다면 false 처리
        userItemRepository.findByUserAndItemType(user, request.getItem_type())
                .ifPresent(UserItem::cancelItem);

        //3. 해당 카테고리의 아이템을 적용

        //해당 카테고리, 이름의 아이템 찾기
        Store store = storeRepository.findByItemTypeAndName(request.getItem_type(), request.getName())
                .orElseThrow(()->new StoreItemNotExistsException("상점에 카테고리, 이름에 해당하는 상품이 존재하지 않습니다."));

        //해당 카테고리, 이름의 아이템을 구매한 적이 있는지 검사
        UserStore userStore = userStoreRepository.findByUserAndStore(user, store);
        if(userStore == null) throw new StoreItemNotExistsException("상점에서 아이템을 구매한 적이 없습니다.");

        // 해당 카테고리, 이름의 유저 아이템 찾기
        Optional<UserItem> userItem = userItemRepository.findByUserAndItemTypeAndName(user, request.getItem_type(), request.getName());

        if(userItem.isEmpty()){
            UserItem newUserItem = new UserItem(user,store);
            userItemRepository.save(newUserItem);
        }else{
            userItem.get().setItem();
            userItemRepository.save(userItem.get());
        }

        //4. 이번달 한달이에 해당 가구 저장
        Handali handali = handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId());
        if(request.getItem_type()== ItemType.BACKGROUND) handali.setBackground(request.getName());
        else if(request.getItem_type()==ItemType.FLOOR) handali.setFloor(request.getName());
        else if(request.getItem_type()==ItemType.SOFA) handali.setSofa(request.getName());
        else if(request.getItem_type()==ItemType.WALL) handali.setWall(request.getName());
        handaliRepository.save(handali);
    }
}
