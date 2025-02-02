package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatDetailDTO {
    private TypeName typeName;
    private float value;
}

