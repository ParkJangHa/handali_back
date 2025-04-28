/*직업 insert*/
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