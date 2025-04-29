/**
  전체 테이블 생성
 */

CREATE TABLE `apart` (
                         `apart_id` int NOT NULL,
                         `floor` int NOT NULL,
                         `user_id` bigint DEFAULT NULL,
                         `nickname` varchar(255) DEFAULT NULL,
                         `handali_id` bigint NOT NULL,
                         `apart_total_id` bigint NOT NULL AUTO_INCREMENT,
                         PRIMARY KEY (`apart_total_id`),
                         UNIQUE KEY `UKjleahx071r89nxew5q7ovxc8k` (`handali_id`),
                         KEY `fk_user` (`user_id`),
                         CONSTRAINT `fk_user` FOREIGN KEY (`handali_id`) REFERENCES `handali` (`handali_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                         CONSTRAINT `FKqgqhbl6tlu8it6wn74fw7whg9` FOREIGN KEY (`handali_id`) REFERENCES `handali` (`handali_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `habit` (
                         `habit_id` bigint NOT NULL AUTO_INCREMENT,
                         `detailed_habit_name` varchar(255) NOT NULL,
                         `category_name` enum('ACTIVITY','ART','INTELLIGENT') NOT NULL,
                         `created_type` enum('DEVELOPER','USER') NOT NULL,
                         PRIMARY KEY (`habit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `handali` (
                           `handali_id` bigint NOT NULL AUTO_INCREMENT,
                           `nickname` varchar(255) NOT NULL,
                           `start_date` date NOT NULL,
                           `job_id` bigint DEFAULT NULL,
                           `user_id` bigint DEFAULT NULL,
                           `image` varchar(255) DEFAULT NULL,
                           `apart_total_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`handali_id`),
                           UNIQUE KEY `UKaq46obet39igw0x11x5d7kk67` (`apart_total_id`),
                           KEY `FK72hbf2m6q0upsd50tmxiio1ou` (`job_id`),
                           KEY `FK32xswqpibn7y9ir6nvmeau6qy` (`user_id`),
                           CONSTRAINT `apart_fk` FOREIGN KEY (`apart_total_id`) REFERENCES `apart` (`apart_total_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT `FK32xswqpibn7y9ir6nvmeau6qy` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT `FK72hbf2m6q0upsd50tmxiio1ou` FOREIGN KEY (`job_id`) REFERENCES `job` (`job_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT `FKbnvoxegcvlcff7nckghn7vp5i` FOREIGN KEY (`apart_total_id`) REFERENCES `apart` (`apart_total_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `handali_stat` (
                                `handali_id` bigint NOT NULL,
                                `handali_stat_id` bigint NOT NULL AUTO_INCREMENT,
                                `stat_id` bigint NOT NULL,
                                PRIMARY KEY (`handali_stat_id`),
                                KEY `FKn2m6f52r8cm9xsltl9yks4rac` (`handali_id`),
                                KEY `FKgespoh1rj3a0f49a9ygnpgmf9` (`stat_id`),
                                CONSTRAINT `FKgespoh1rj3a0f49a9ygnpgmf9` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`stat_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                CONSTRAINT `FKn2m6f52r8cm9xsltl9yks4rac` FOREIGN KEY (`handali_id`) REFERENCES `handali` (`handali_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `job` (
                       `week_salary` int NOT NULL,
                       `job_id` bigint NOT NULL AUTO_INCREMENT,
                       `name` varchar(255) NOT NULL,
                       PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `job_stat` (
                            `required_stat` float NOT NULL,
                            `job_id` bigint NOT NULL,
                            `job_stat_id` bigint NOT NULL AUTO_INCREMENT,
                            `type_name` enum('ACTIVITY_SKILL','ART_SKILL','INTELLIGENT_SKILL') NOT NULL,
                            PRIMARY KEY (`job_stat_id`),
                            KEY `fk_job_stat` (`job_id`),
                            CONSTRAINT `fk_job_stat` FOREIGN KEY (`job_id`) REFERENCES `job` (`job_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `record` (
                          `date` date NOT NULL,
                          `satisfaction` int NOT NULL,
                          `time` float NOT NULL,
                          `habit_id` bigint DEFAULT NULL,
                          `record_id` bigint NOT NULL AUTO_INCREMENT,
                          `user_id` bigint DEFAULT NULL,
                          PRIMARY KEY (`record_id`),
                          UNIQUE KEY `UKoaebw3pbp7trh5t4tbhoxjkd6` (`habit_id`,`date`,`user_id`),
                          KEY `fk_record_user` (`user_id`),
                          CONSTRAINT `fk_record_habit` FOREIGN KEY (`habit_id`) REFERENCES `habit` (`habit_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                          CONSTRAINT `fk_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `stat` (
                        `last_month_value` float NOT NULL DEFAULT '0',
                        `value` float NOT NULL DEFAULT '0',
                        `stat_id` bigint NOT NULL AUTO_INCREMENT,
                        `type_name` enum('ACTIVITY_SKILL','ART_SKILL','INTELLIGENT_SKILL') NOT NULL,
                        PRIMARY KEY (`stat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `store` (
                         `price` int NOT NULL,
                         `store_id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(255) NOT NULL,
                         `item_type` enum('BACKGROUND','FLOOR','SOFA','WALL') NOT NULL,
                         PRIMARY KEY (`store_id`),
                         UNIQUE KEY `UKd0p5ly1cv6guij7sq1mbnr8ec` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `user` (
                        `birthday` date NOT NULL,
                        `total_coin` int NOT NULL DEFAULT '0',
                        `user_id` bigint NOT NULL AUTO_INCREMENT,
                        `email` varchar(255) NOT NULL,
                        `name` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `phone` varchar(255) NOT NULL,
                        PRIMARY KEY (`user_id`),
                        UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `user_habit` (
                              `month` int DEFAULT NULL,
                              `habit_id` bigint NOT NULL,
                              `user_habit_id` bigint NOT NULL AUTO_INCREMENT,
                              `user_id` bigint NOT NULL,
                              PRIMARY KEY (`user_habit_id`),
                              KEY `FKogvrcxpxm129u61cpxs1ele8a` (`habit_id`),
                              KEY `FK7wmt5q5sh751wci2tm5el392d` (`user_id`),
                              CONSTRAINT `FK7wmt5q5sh751wci2tm5el392d` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
                              CONSTRAINT `FKogvrcxpxm129u61cpxs1ele8a` FOREIGN KEY (`habit_id`) REFERENCES `habit` (`habit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `user_item` (
                             `user_item_id` bigint NOT NULL AUTO_INCREMENT,
                             `is_available` bit(1) NOT NULL,
                             `store_id` bigint DEFAULT NULL,
                             `user_id` bigint DEFAULT NULL,
                             PRIMARY KEY (`user_item_id`),
                             KEY `fk_item_store` (`store_id`),
                             KEY `fk_item_user` (`user_id`),
                             CONSTRAINT `fk_item_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `fk_item_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `user_store` (
                              `store_id` bigint DEFAULT NULL,
                              `user_id` bigint DEFAULT NULL,
                              `user_store_id` bigint NOT NULL AUTO_INCREMENT,
                              PRIMARY KEY (`user_store_id`),
                              KEY `fk_store_store` (`store_id`),
                              KEY `fk_store_user` (`user_id`),
                              CONSTRAINT `fk_store_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                              CONSTRAINT `fk_store_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

/**
  habit 초기 데이터
 */

 -- ACTIVITY 카테고리 (예: 액티비티)
insert into habit(category_name, detailed_habit_name, created_type) values('ACTIVITY', '등산', 'DEVELOPER');
insert into habit(category_name, detailed_habit_name, created_type) values('ACTIVITY', '자전거 타기', 'DEVELOPER');
insert into habit(category_name, detailed_habit_name, created_type) values('ACTIVITY', '조깅', 'DEVELOPER');

-- ART 카테고리 (예: 아트)
insert into habit(category_name, detailed_habit_name, created_type) values('ART', '그림 그리기', 'DEVELOPER');
insert into habit(category_name, detailed_habit_name, created_type) values('ART', '사진 찍기', 'DEVELOPER');
insert into habit(category_name, detailed_habit_name, created_type) values('ART', '조각 만들기', 'DEVELOPER');

-- INTELLIGENT 카테고리 (예: 인텔리전트)
insert into habit(category_name, detailed_habit_name, created_type) values('INTELLIGENT', '독서하기', 'DEVELOPER');
insert into habit(category_name, detailed_habit_name, created_type) values('INTELLIGENT', '퍼즐 풀기', 'DEVELOPER');
insert into habit(category_name, detailed_habit_name, created_type) values('INTELLIGENT', '코딩 연습', 'DEVELOPER');

/**
  job 초기 데이터
 */

-- 활동 (Activity)
INSERT INTO job (name, week_salary) VALUES ('축구선수', 3000);
INSERT INTO job (name, week_salary) VALUES ('항공기조종사', 2000);
INSERT INTO job (name, week_salary) VALUES ('군인', 1200);
INSERT INTO job (name, week_salary) VALUES ('소방관', 900);
INSERT INTO job (name, week_salary) VALUES ('경찰', 800);
INSERT INTO job (name, week_salary) VALUES ('경호원', 700);
INSERT INTO job (name, week_salary) VALUES ('여행지가이드', 600);
INSERT INTO job (name, week_salary) VALUES ('사육사', 500);
INSERT INTO job (name, week_salary) VALUES ('체육교사', 400);
INSERT INTO job (name, week_salary) VALUES ('농부', 300);

INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (1, 'ACTIVITY_SKILL', 95); -- 축구선수
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (2, 'ACTIVITY_SKILL', 85); -- 항공기조종사
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (3, 'ACTIVITY_SKILL', 75); -- 군인
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (4, 'ACTIVITY_SKILL', 70); -- 소방관
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (5, 'ACTIVITY_SKILL', 65); -- 경찰
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (6, 'ACTIVITY_SKILL', 60); -- 경호원
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (7, 'ACTIVITY_SKILL', 55); -- 여행지가이드
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (8, 'ACTIVITY_SKILL', 50); -- 사육사
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (9, 'ACTIVITY_SKILL', 40); -- 체육교사
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (10, 'ACTIVITY_SKILL', 30); -- 농부

-- 예술 (Art) - 실제 연봉 기준 내림차순 정렬
INSERT INTO job (name, week_salary) VALUES ('영화감독', 2500);  -- 연봉 약 12.5억 원
INSERT INTO job (name, week_salary) VALUES ('화가', 2000);  -- 연봉 약 10억 원
INSERT INTO job (name, week_salary) VALUES ('음악가', 1800);  -- 연봉 약 9억 원
INSERT INTO job (name, week_salary) VALUES ('무용가', 1500);  -- 연봉 약 7.5억 원
INSERT INTO job (name, week_salary) VALUES ('소설가', 1200);  -- 연봉 약 6억 원
INSERT INTO job (name, week_salary) VALUES ('웹툰 작가', 1000);  -- 연봉 약 5억 원
INSERT INTO job (name, week_salary) VALUES ('디자이너', 800);  -- 연봉 약 4억 원
INSERT INTO job (name, week_salary) VALUES ('피아니스트', 600);  -- 연봉 약 3억 원
INSERT INTO job (name, week_salary) VALUES ('사진작가', 500);  -- 연봉 약 2.5억 원
INSERT INTO job (name, week_salary) VALUES ('서예가', 300);  -- 연봉 약 1.5억 원

INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (11, 'ART_SKILL', 95); -- 영화감독
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (12, 'ART_SKILL', 90); -- 화가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (13, 'ART_SKILL', 85); -- 음악가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (14, 'ART_SKILL', 80); -- 무용가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (15, 'ART_SKILL', 75); -- 소설가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (16, 'ART_SKILL', 70); -- 웹툰 작가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (17, 'ART_SKILL', 65); -- 디자이너
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (18, 'ART_SKILL', 60); -- 공예가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (19, 'ART_SKILL', 50); -- 사진작가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (20, 'ART_SKILL', 30); -- 서예가

-- 지능 (Intelligence) - 실제 연봉 기준 내림차순 정렬
INSERT INTO job (name, week_salary) VALUES ('의사', 3000);  -- 연봉 약 15억 원
INSERT INTO job (name, week_salary) VALUES ('변호사', 2500);  -- 연봉 약 12.5억 원
INSERT INTO job (name, week_salary) VALUES ('과학자', 2000);  -- 연봉 약 10억 원
INSERT INTO job (name, week_salary) VALUES ('엔지니어', 1800);  -- 연봉 약 9억 원
INSERT INTO job (name, week_salary) VALUES ('교수', 1500);  -- 연봉 약 7.5억 원
INSERT INTO job (name, week_salary) VALUES ('회계사', 1200);  -- 연봉 약 6억 원
INSERT INTO job (name, week_salary) VALUES ('데이터분석가', 1000);  -- 연봉 약 5억 원
INSERT INTO job (name, week_salary) VALUES ('심리학자', 800);  -- 연봉 약 4억 원
INSERT INTO job (name, week_salary) VALUES ('도서관 사서', 500);  -- 연봉 약 2.5억 원
INSERT INTO job (name, week_salary) VALUES ('비서', 300);  -- 연봉 약 1.5억 원

-- 지적 스탯 요구사항 - 연봉 비례 스탯
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (21, 'INTELLIGENT_SKILL', 95); -- 의사
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (22, 'INTELLIGENT_SKILL', 90); -- 변호사
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (23, 'INTELLIGENT_SKILL', 85); -- 과학자
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (24, 'INTELLIGENT_SKILL', 80); -- 엔지니어
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (25, 'INTELLIGENT_SKILL', 75); -- 교수
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (26, 'INTELLIGENT_SKILL', 70); -- 회계사
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (27, 'INTELLIGENT_SKILL', 65); -- 데이터분석가
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (28, 'INTELLIGENT_SKILL', 60); -- 교사
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (29, 'INTELLIGENT_SKILL', 50); -- 도서관 사서
INSERT INTO job_stat (job_id, type_name, required_stat) VALUES (30, 'INTELLIGENT_SKILL', 30); -- 비서

-- 백수
INSERT INTO job (name, week_salary) VALUES ('백수', 0);

/**
  store 초기 데이터
 */

-- BACKGROUND
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

insert into store(price, name, item_type) values (0, '배경없음', 'BACKGROUND');
insert into store(price, name, item_type) values (0, '소파없음', 'SOFA');
insert into store(price, name, item_type) values (0, '벽장식없음', 'WALL');
insert into store(price, name, item_type) values (0, '바닥장식없음', 'FLOOR');