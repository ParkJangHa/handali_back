package com.handalsali.handali.DTO;


import com.handalsali.handali.enums.ItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoreDTO {
    @Data
    @AllArgsConstructor
    public static class StoreViewResponse{
        private Long storeId;
        private ItemType item_type;
        private String name;
        private int price;
        private boolean isBuy;
    }


    @Data
    public static class StoreBuyRequest{
        @NotBlank(message = "카테고리는 필수입니다.")
        private ItemType item_type;
        @NotBlank(message = "아이템 이름은 필수입니다.")
        private String name;
    }
}
