
DELETE FROM habit where created_type='DEVELOPER';

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
