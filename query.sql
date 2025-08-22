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


# 0819 (D_Post & D_Comment)
CREATE TABLE IF NOT EXISTS `posts`(
	`id` 		bigint not null auto_increment,
    `title` 	varchar(200) not null comment '게시글 제목',
    `content` 	longtext not null comment '게시글 내용', # -- @Lob 매핑 대응
    `author` 	varchar(100) not null comment '작성자 표시명 또는 ID',
    
    primary key(`id`),
    key `idx_post_author` (`author`) # 저자를 기준으로 찾고싶어요. 
    
)   engine=InnoDb
	DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    comment = '게시글';
    
-- 댓글 테이블
CREATE TABLE IF NOT EXISTS `comments`(
	`id` 		bigint not null auto_increment,
    `post_id` 	bigint not null comment 'posts.id FK', # posts 테이블의 id를 참조 (외래키 설정 예정)
    `content`	varchar(1000) not null comment '댓글 내용',
    `commenter` varchar(100) not null comment '댓글 작성자 표시명 또는 id',
    
    primary key (`id`),
    key `idx_comment_post_id` (`post_id`), -- post_id에 인덱스 설정 # 댓글 아이디로 정렬
    key `idx_comment_commenter` (`commenter`), # 댓글 작성자로 정렬
    constraint `fk_comment_post`
		foreign key (`post_id`) references `posts` (`id`) on delete cascade on update cascade
        
) 	engine=InnoDb
	DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci # 대소문자 구분 안함
    comment = '댓글'; # 테이블 자체에 대한 설명
    
select * from `posts`;
select * from `comments`;

#0821 (F_Board)
-- 게시판 테이블(생성/수정 시간 포함)
CREATE TABLE IF NOT EXISTS `boards`(
	id BIGINT AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    content LONGTEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    
    PRIMARY KEY (`id`)
) 	ENGINE=InnoDB
	DEFAULT CHARACTER SET = utf8mb4
    COLLATE utf8mb4_unicode_ci
    COMMENT = '게시글';

SELECT * FROM `boards`;

#0822 

    
USE k5_iot_springboot;
    