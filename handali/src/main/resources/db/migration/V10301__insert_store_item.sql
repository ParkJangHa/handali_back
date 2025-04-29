
DELETE FROM store;
ALTER TABLE store AUTO_INCREMENT = 1;

-- BACKGROUND
insert into store(price, name, item_type) values (0, '배경없음', 'BACKGROUND');
insert into store(price, name, item_type) values (0, '소파없음', 'SOFA');
insert into store(price, name, item_type) values (0, '벽장식없음', 'WALL');
insert into store(price, name, item_type) values (0, '바닥장식없음', 'FLOOR');


INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '원룸', 10000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '빌라', 15000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '아파트', 20000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '스위트룸', 30000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '고풍스러운 서재', 25000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '미니멀리스트 거실', 18000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '화려한 펜트하우스', 40000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '일본식 다다미방', 12000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '유럽풍 클래식 인테리어', 35000);
INSERT INTO store (item_type, name, price) VALUES ('BACKGROUND', '따뜻한 카페 스타일 공간', 22000);

-- SOFA
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '나무 의자', 5000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '철제 의자', 7000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '디자인 의자', 12000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '나무 소파', 15000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '푹신한 소파', 20000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '철제 소파', 18000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '디자인 소파', 25000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '가죽 소파', 30000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '모듈형 소파', 35000);
INSERT INTO store (item_type, name, price) VALUES ('SOFA', '빈티지 패브릭 소파', 28000);

-- WALL
INSERT INTO store (item_type, name, price) VALUES ('WALL', '나무 시계', 8000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '철 창문', 12000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '값싼 액자', 5000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '비싼 액자', 20000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '모던한 벽걸이 선반', 15000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '빈티지 거울', 18000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', 'LED 네온 사인', 25000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '그림 액자', 10000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '벽걸이 플랜트', 22000);
INSERT INTO store (item_type, name, price) VALUES ('WALL', '세계 지도 장식', 30000);

-- FLOOR
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '스탠딩 조명', 12000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '크리스마스 트리', 15000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '모던 러그', 18000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '대형 화분', 20000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '빈티지 서랍장', 25000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '자동 로봇 청소기', 40000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '책 무더기', 5000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '불멍용 미니 화로', 28000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '전신 거울', 22000);
INSERT INTO store (item_type, name, price) VALUES ('FLOOR', '앤틱 보석함', 35000);
