package com.handalsali.handali.DTO;

import com.handalsali.handali.enums_multyKey.TypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatDetailDTO {
    @Schema(description = "스탯 이름", example = "ACTIVITY_SKILL")
    private TypeName typeName;
    @Schema(description = "스탯 수치", example = "30.5")
    private float value;
}

