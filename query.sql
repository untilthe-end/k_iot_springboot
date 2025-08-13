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

#0813 (C_Book)
drop table if exists `books`;

CREATE TABLE IF NOT EXISTS `books` (
	id bigint primary key auto_increment,
    writer varchar(50) not null,
    title varchar(100) not null,
    content varchar(500) not null,
    category varchar(20) not null,
    
    # 자바 enum 데이터 처리
    # : DB에서는 VARCHAR(문자열)로 관리 + CHECK 제약 조건으로 문자 제한
    constraint chk_book_category check (category In ('NOVEL', 'ESSAY', 'POEM', 'MAGAZINE')),
    # 같은 저자 + 동일 제목 중복 저장 방지 
    constraint uk_book_writer_title UNIQUE (writer, title)
);

select * from books;