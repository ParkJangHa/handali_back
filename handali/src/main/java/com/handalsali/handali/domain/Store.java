package com.handalsali.handali.domain;

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
    private String category; //배경, 소파, 벽장식, 바닥장식

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false)
    private int price;
}