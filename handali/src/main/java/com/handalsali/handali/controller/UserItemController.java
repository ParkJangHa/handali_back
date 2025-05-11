package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.UserItemDTO;
import com.handalsali.handali.service.UserItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class UserItemController {
    private final UserItemService userItemService;
    private final BaseController baseController;

    @PostMapping("/set")
    public ResponseEntity<String> setUserItem(@RequestHeader("Authorization")String accessToken,
                                              @RequestBody UserItemDTO.SetUserItemRequest request) {
        String token = baseController.extraToken(accessToken);
        userItemService.setUserItem(token, request);

        return ResponseEntity.ok("아이템을 적용했습니다.");
    }
}
