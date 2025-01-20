package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatDetail {
    private TypeName type_Name; // 스탯 이름
    private double value;      // 스탯 값
}
