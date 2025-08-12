### k5_iot_springboot >>> query ###

# 1. 스키마 생성 (이미 존재하면 삭제)
DROP DATABASE IF EXISTS `k5_iot_springboot`;

# 2. 스키마 생성 + 문자셋/정렬 설정
CREATE DATABASE IF NOT EXISTS `k5_iot_springboot`
	character SET utf8mb4
    COLLATE utf8mb4_general_ci;
    
# 3. 스키마 선택
USE k5_iot_springboot;

# 0811 (A_Test)
CREATE TABLE IF NOT EXISTS `test` (
	test_id bigint primary key auto_increment,
    name varchar(50) not null
);

select * from test;

# 0812 (B_Student)
drop table if exists `students`;

create TABLE IF not EXISTS `students` (
	id bigint primary key auto_increment,
    name varchar(100) not null,
    email varchar(100) not null unique,
	unique key uq_name_email (name, email)
    # : name + email 조합이 유일하도록 설정
);

select * from `students`;