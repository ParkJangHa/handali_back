package com.handalsali.handali.DTO;


import com.handalsali.handali.enums.ItemType;
import lombok.Data;

public class UserItemDTO {
    @Data
    public static class SetUserItemRequest{
        private ItemType item_type;
        private String name;
    }
}
