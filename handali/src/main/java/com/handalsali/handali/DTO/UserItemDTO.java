package com.handalsali.handali.DTO;


import com.handalsali.handali.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;

public class UserItemDTO {
    @Data
    @AllArgsConstructor
    public static class SetUserItemRequest{
        private ItemType item_type;
        private String name;
    }
}
