package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "store_items")
@Getter @Setter
public class StoreItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 자동 증가
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(name = "is_buy", nullable = false)
    private boolean isBuy;  // 사용자가 구매한 여부
}