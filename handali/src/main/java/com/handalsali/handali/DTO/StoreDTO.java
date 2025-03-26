package com.handalsali.handali.DTO;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDTO {
    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;
    @NotBlank(message = "아이템 이름은 필수입니다.")
    private String name;
    @Min(value = 1, message = "가격은 1 이상이어야 합니다.")
    private int price;
}
