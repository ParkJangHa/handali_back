package com.handalsali.handali.domain;

import com.handalsali.handali.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 자동 증가
    private Long storeId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false)
    private int price;
}