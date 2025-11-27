-- ================================================
-- 직업 데이터
-- ================================================

-- 활동 (Activity) 1~10
INSERT INTO job (job_id, name, week_salary)
SELECT 1, '축구선수', 3000 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 1);

INSERT INTO job (job_id, name, week_salary)
SELECT 2, '항공기조종사', 2000 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 2);

INSERT INTO job (job_id, name, week_salary)
SELECT 3, '군인', 1200 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 3);

INSERT INTO job (job_id, name, week_salary)
SELECT 4, '소방관', 900 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 4);

INSERT INTO job (job_id, name, week_salary)
SELECT 5, '경찰', 800 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 5);

INSERT INTO job (job_id, name, week_salary)
SELECT 6, '경호원', 700 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 6);

INSERT INTO job (job_id, name, week_salary)
SELECT 7, '여행지가이드', 600 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 7);

INSERT INTO job (job_id, name, week_salary)
SELECT 8, '사육사', 500 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 8);

INSERT INTO job (job_id, name, week_salary)
SELECT 9, '체육교사', 400 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 9);

INSERT INTO job (job_id, name, week_salary)
SELECT 10, '농부', 300 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 10);

-- 예술 (Art) 11~20
INSERT INTO job (job_id, name, week_salary)
SELECT 11, '영화감독', 2500 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 11);

INSERT INTO job (job_id, name, week_salary)
SELECT 12, '화가', 2000 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 12);

INSERT INTO job (job_id, name, week_salary)
SELECT 13, '음악가', 1800 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 13);

INSERT INTO job (job_id, name, week_salary)
SELECT 14, '무용가', 1500 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 14);

INSERT INTO job (job_id, name, week_salary)
SELECT 15, '소설가', 1200 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 15);

INSERT INTO job (job_id, name, week_salary)
SELECT 16, '웹툰 작가', 1000 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 16);

INSERT INTO job (job_id, name, week_salary)
SELECT 17, '디자이너', 800 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 17);

INSERT INTO job (job_id, name, week_salary)
SELECT 18, '피아니스트', 600 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 18);

INSERT INTO job (job_id, name, week_salary)
SELECT 19, '사진작가', 500 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 19);

INSERT INTO job (job_id, name, week_salary)
SELECT 20, '서예가', 300 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 20);

-- 지능 (Intelligence) 21~30
INSERT INTO job (job_id, name, week_salary)
SELECT 21, '의사', 3000 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 21);

INSERT INTO job (job_id, name, week_salary)
SELECT 22, '변호사', 2500 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 22);

INSERT INTO job (job_id, name, week_salary)
SELECT 23, '과학자', 2000 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 23);

INSERT INTO job (job_id, name, week_salary)
SELECT 24, '엔지니어', 1800 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 24);

INSERT INTO job (job_id, name, week_salary)
SELECT 25, '교수', 1500 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 25);

INSERT INTO job (job_id, name, week_salary)
SELECT 26, '회계사', 1200 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 26);

INSERT INTO job (job_id, name, week_salary)
SELECT 27, '데이터분석가', 1000 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 27);

INSERT INTO job (job_id, name, week_salary)
SELECT 28, '심리학자', 800 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 28);

INSERT INTO job (job_id, name, week_salary)
SELECT 29, '도서관 사서', 500 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 29);

INSERT INTO job (job_id, name, week_salary)
SELECT 30, '비서', 300 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 30);

-- 백수 31
INSERT INTO job (job_id, name, week_salary)
SELECT 31, '백수', 0 WHERE NOT EXISTS (SELECT 1 FROM job WHERE job_id = 31);

-- ================================================
-- 직업 스탯 요구사항
-- ================================================

-- 활동 (Activity)
INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 1, 'ACTIVITY_SKILL', 95 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 1 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 2, 'ACTIVITY_SKILL', 85 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 2 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 3, 'ACTIVITY_SKILL', 75 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 3 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 4, 'ACTIVITY_SKILL', 70 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 4 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 5, 'ACTIVITY_SKILL', 65 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 5 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 6, 'ACTIVITY_SKILL', 60 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 6 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 7, 'ACTIVITY_SKILL', 55 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 7 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 8, 'ACTIVITY_SKILL', 50 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 8 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 9, 'ACTIVITY_SKILL', 40 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 9 AND type_name = 'ACTIVITY_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 10, 'ACTIVITY_SKILL', 30 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 10 AND type_name = 'ACTIVITY_SKILL');

-- 예술 (Art)
INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 11, 'ART_SKILL', 95 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 11 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 12, 'ART_SKILL', 90 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 12 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 13, 'ART_SKILL', 85 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 13 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 14, 'ART_SKILL', 80 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 14 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 15, 'ART_SKILL', 75 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 15 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 16, 'ART_SKILL', 70 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 16 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 17, 'ART_SKILL', 65 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 17 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 18, 'ART_SKILL', 60 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 18 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 19, 'ART_SKILL', 50 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 19 AND type_name = 'ART_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 20, 'ART_SKILL', 30 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 20 AND type_name = 'ART_SKILL');

-- 지능 (Intelligence)
INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 21, 'INTELLIGENT_SKILL', 95 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 21 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 22, 'INTELLIGENT_SKILL', 90 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 22 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 23, 'INTELLIGENT_SKILL', 85 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 23 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 24, 'INTELLIGENT_SKILL', 80 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 24 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 25, 'INTELLIGENT_SKILL', 75 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 25 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 26, 'INTELLIGENT_SKILL', 70 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 26 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 27, 'INTELLIGENT_SKILL', 65 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 27 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 28, 'INTELLIGENT_SKILL', 60 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 28 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 29, 'INTELLIGENT_SKILL', 50 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 29 AND type_name = 'INTELLIGENT_SKILL');

INSERT INTO job_stat (job_id, type_name, required_stat)
SELECT 30, 'INTELLIGENT_SKILL', 30 WHERE NOT EXISTS (SELECT 1 FROM job_stat WHERE job_id = 30 AND type_name = 'INTELLIGENT_SKILL');