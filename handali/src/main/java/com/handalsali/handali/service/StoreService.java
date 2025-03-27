package com.handalsali.handali.service;

import com.handalsali.handali.DTO.StoreDTO;
import com.handalsali.handali.domain.Store;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserStore;
import com.handalsali.handali.exception.InsufficientCoinException;
import com.handalsali.handali.exception.StoreItemAlreadyBoughtException;
import com.handalsali.handali.exception.StoreItemNotExistsException;
import com.handalsali.handali.repository.StoreRepository;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.repository.UserStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final UserService userService;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserStoreRepository userStoreRepository;

    /**
     * [카테고리별
     * 상점 물품 조회]
     */
    public List<StoreDTO.StoreViewResponse> storeViewByCategory(String token, String category) {
        //1. 사용자 찾기
        User user=userService.tokenToUser(token);

        //2. 상점 물품 전체 조회하여 리스트로 반환
        List<Store> stores=storeRepository.findByCategory(category);
        if(stores.isEmpty()) throw new StoreItemNotExistsException("상점에 해당 카테고리의 물품이 존재하지 않습니다.");

        //3. 유저상점 물품에 존재하면 is_Buy가 true 아니면 false 하여 저장
        List<StoreDTO.StoreViewResponse> responses=new ArrayList<>();
        for(Store store:stores) {
            UserStore userStore = userStoreRepository.findByUserAndStore(user, store);

            boolean isBuy= userStore!=null;

            StoreDTO.StoreViewResponse response=new StoreDTO.StoreViewResponse(
                    store.getStoreId(),
                    store.getCategory(),
                    store.getName(),
                    store.getPrice(),
                    isBuy
            );

            responses.add(response);
        }

        return responses;
    }

    /**
     * [상점 물품 구매]
     */
    public String storeBuyItem(String token,StoreDTO.StoreBuyRequest request){
        //1. 사용자 찾기
        User user=userService.tokenToUser(token);

        //2. 카테고리, 이름이 동일한 상품 찾기
        Store store = storeRepository.findByCategoryAndName(request.getCategory(), request.getName())
                .orElseThrow(()->new StoreItemNotExistsException("상점에 카테고리, 이름에 해당하는 상품이 존재하지 않습니다."));

        if(userStoreRepository.findByUserAndStore(user, store)!=null) throw new StoreItemAlreadyBoughtException("이미 구매한 아이템입니다.");

        //3. 사용자 코인과 상품 가격 비교
        int price=store.getPrice();
        int userTotalCoin = user.getTotal_coin();

        if(price>userTotalCoin) throw new InsufficientCoinException("코인이 부족합니다.");

        UserStore userStore=new UserStore(user,store);
        userStoreRepository.save(userStore);

        user.setTotalCoin(userTotalCoin-price);
        userRepository.save(user);

        return "아이템을 구매했습니다.";

    }
}
