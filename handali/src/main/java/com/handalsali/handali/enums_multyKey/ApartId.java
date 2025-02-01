package com.handalsali.handali.enums_multyKey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class ApartId implements Serializable {
    @Column(name="apart_id")
    private int apartId;
    private int floor;

    // equals()와 hashCode() 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApartId apartId = (ApartId) o;
        return Objects.equals(apartId, apartId.apartId) && Objects.equals(floor, apartId.floor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apartId, floor);
    }

    public int getApartId() {
        return apartId;
    }

    public int getFloor() {
        return floor;
    }
}
