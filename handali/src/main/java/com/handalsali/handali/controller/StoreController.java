package com.handalsali.handali.controller;

import com.handalsali.handali.DTO.StoreDTO;
import com.handalsali.handali.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final BaseController baseController;
    private final StoreService storeService;

    public StoreController(BaseController baseController, StoreService storeService) {
        this.baseController = baseController;
        this.storeService = storeService;
    }

    @GetMapping("/view")
    public ResponseEntity<List<StoreDTO.StoreViewResponse>> storeViewByCategory(@RequestParam String category,
                                                      @RequestHeader("Authorization") String accessToken) {
        String token = baseController.extraToken(accessToken);

        List<StoreDTO.StoreViewResponse> responses = storeService.storeViewByCategory(token, category);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/buy")
    public ResponseEntity<String> storeBuyItem(@RequestHeader("Authorization") String accessToken,
                                               @RequestBody StoreDTO.StoreBuyRequest request){
        String token = baseController.extraToken(accessToken);

        String response = storeService.storeBuyItem(token, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
