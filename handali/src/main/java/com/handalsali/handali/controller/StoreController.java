package com.handalsali.handali.controller;

import com.handalsali.handali.domain.StoreItem;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.service.StoreService;
import com.handalsali.handali.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final BaseController baseController;
    private final UserService userService;

    // 카테고리별 상점 물품 조회
    @GetMapping("/view")
    public ResponseEntity<List<StoreItem>> getStoreItems(
            @RequestParam String category,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);

        return ResponseEntity.ok(storeService.getItemsByCategory(category, token));
    }

    // 상점 물품 구매
    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>> buyItem(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);

        String category = (String) request.get("category");
        String name = (String) request.get("name");
        int price = (request.get("price") instanceof Integer)
                ? (Integer) request.get("price")
                : Integer.parseInt(request.get("price").toString());

        // 유저 정보 조회
        User user = userService.tokenToUser(token);

        // 현재 유저의 총 코인
        int userTotalCoin = user.getTotal_coin();

        // 상점에서 아이템을 찾기
        StoreItem storeItem = storeService.getItemByCategoryAndName(category, name);
        if (storeItem == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "아이템을 찾을 수 없습니다."));
        }

        // 코인 비교
        if (userTotalCoin < price) {
            return ResponseEntity.badRequest().body(Map.of("message", "코인이 부족합니다."));
        }
        //아이템 구매 처리
        String message = storeService.buyItem(category, name, price, token);

        // 성공적으로 구매 처리된 경우
        if (message.equals("아이템을 구매했습니다.")) {
            // 코인 차감
            user.setTotalCoin(user.getTotal_coin() - price);
            userService.save(user);  // 변경된 유저 정보 저장
            return ResponseEntity.ok(Map.of("message", message));
        }
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
