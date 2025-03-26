package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.StoreDTO;
import com.handalsali.handali.domain.StoreItem;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.service.StoreService;
import com.handalsali.handali.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    // 카테고리별 상점 물품 조회
    @Operation(summary = "상점 물품 조회", description = "카테고리별 상점 물품 조회")
    @GetMapping("/view")
    public ResponseEntity<List<StoreItem>> getStoreItems(
            @RequestParam String category,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        return ResponseEntity.ok(storeService.getItemsByCategory(category));
    }

    // 상점 물품 구매
    @Operation(summary = "상점 물품 구매", description = "상점 물품 구매")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "물품 구매 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message":"아이템을 구매했습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "이미 구매한 물품",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미 구매한 아이템입니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "유저 토탈 코인 부족",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message":"코인이 부족합니다."
                                    }
                                    """)))
    })
    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>> buyItem(
            @Valid @RequestBody StoreDTO storeDTO,
            @RequestHeader("Authorization") String accessToken) {

        String token = baseController.extraToken(accessToken);
        String message = storeService.buyItem(storeDTO, token);

        // 응답 반환
        return message.equals("아이템을 구매했습니다.") ?
                ResponseEntity.ok(Map.of("message", message)) :
                ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
