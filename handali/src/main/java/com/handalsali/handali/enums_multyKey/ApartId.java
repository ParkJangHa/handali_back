package com.handalsali.handali.enums_multyKey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
@EqualsAndHashCode
public class ApartId implements Serializable {
    @Column(name="apart_id")
    private Long apartId;

    @Column(name = "floor", insertable=false, updatable=false)
    private int floor;  // 층수 (1~12)

    // equals()와 hashCode() 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApartId apartId = (ApartId) o;
        //수정 - 02/16
        return floor == apartId.floor && Objects.equals(this.apartId, apartId.apartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apartId, floor);
    }


}
