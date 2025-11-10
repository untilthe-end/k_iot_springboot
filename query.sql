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

# 0825
-- 사용자 테이블

drop table if exists `users`;
CREATE TABLE IF NOT EXISTS `users`(
	id Bigint not null auto_increment,
    login_id varchar(50) not null,
    password varchar(255) not null,
    email varchar(255) not null,
    nickname varchar(50) not null,
    gender varchar(10),
    # 아래 두개는 User Entity 에 없지만 만들어줘야함. @JpaAuditing 했으니
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint `uk_users_login_id` unique (login_id),
	constraint `uk_users_email` unique (email),
	constraint `uk_users_nickname` unique (nickname),
    constraint `chk_users_gender` check(gender in ('MALE', 'FEMALE'))
    

) 	ENGINE=InnoDB
	DEFAULT CHARACTER SET = utf8mb4
    COLLATE utf8mb4_unicode_ci
    COMMENT = '사용자';

# 0910 (G_Role)
-- 권한 코드 테이블
CREATE TABLE IF NOT EXISTS `roles`(
   role_name   VARCHAR(30) PRIMARY KEY
)ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '권한 코드(USER, MANAGER, OWNER 등)';
  
# 0910 (G_UserRoleId)
-- 사용자-권한 매핑 (조인 엔티티)
# DROP TABLE IF EXISTS `user_roles`; (기존의 user_roles 제거)

CREATE TABLE IF NOT EXISTS `user_roles`(
   user_id BIGINT NOT NULL,
    role_name VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_name) REFERENCES roles(role_name)
)ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '사용자 권한 매핑';
  
## 권한 데이터 삽입 ##
INSERT INTO roles (role_name) VALUES
   ('USER'),
   ('MANAGER'),
   ('ADMIN')
    # 이미 값이 있는 경우(DUPLICATE, 중복)
    # , 에러 대신 그대로 유지할 것을 설정
    ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);
    
SELECT * FROM roles;

## 사용자 권한 매핑 삽입 ##
INSERT INTO user_roles (user_id, role_name) VALUES
   (1, 'USER'),
   (2, 'USER'),
    (2, 'MANAGER'),
    (3, 'MANAGER'),
    (3, 'ADMIN')
    ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

SELECT * FROM `users`;

SELECT * FROM `user_roles`;

#### 사용하지 않음 #########################################################
# : 위의 사용자-권한 다대다 형식 사용 권장
# 0827 (G_User_role)
-- 사용자 권한 테이블

-- drop table if exists `user_roles`;
-- CREATE TABLE IF NOT EXISTS `user_roles`(
-- 	user_id BIGINT NOT NULL,
--     role varchar(30) not null,
--     
--     constraint fk_user_roles_user
--     foreign key (user_id) references `users`(id) on delete cascade,
--         
-- 	constraint uk_user_roles unique (user_id, role),
--     
--     constraint chk_user_roles_role check (role in ('USER','MANAGER','ADMIN'))

-- ) 	ENGINE=InnoDB
-- 	DEFAULT CHARACTER SET = utf8mb4
--     COLLATE utf8mb4_unicode_ci
--     COMMENT = '사용자 권한';



# 회원가입은 여기서 못해!! 비밀번호 암호화처리를 했기때문에 ~
# 관리자 권한 부여 Sample Data #　
INSERT INTO user_roles (user_id, role_name)
values (6, "ADMIN"); 

SELECT * FROM `user_roles`;
select * from `users`;

# 0828 (H_Article)
-- 기사 테이블 


drop table if exists `articles`;
CREATE TABLE IF NOT EXISTS `articles`(
	id BIGINT auto_increment,
    title varchar(200) not null,
    content LONGTEXT NOT NULL,
    author_id bigint not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_articles_author
		foreign key (author_id) references `users`(id)
        on delete cascade
  
) 	ENGINE=InnoDB
	DEFAULT CHARACTER SET = utf8mb4
    COLLATE utf8mb4_unicode_ci
    COMMENT = '기사글';
    
SELECT * FROM `articles`;
INSERT INTO articles (title, content, author_id, created_at, updated_at)
VALUES
('기사1', '내용1', 1, NOW(6), NOW(6)),
('기사2', '내용2', 2, NOW(6), NOW(6)),
('기사3', '내용3', 1, NOW(6), NOW(6)),
('기사4', '내용4', 3, NOW(6), NOW(6)),
('기사5', '내용5', 1, NOW(6), NOW(6)),
('기사6', '내용6', 3, NOW(6), NOW(6));

-- 0901 (주문 관리 시스템)
-- 트랜잭션, 트리거, 인덱스, 뷰 학습
# products(상품), stocks(재고)
# , orders(주문 정보), order_items(주문 상세 정보), order_logs(주문 기록 정보)

-- 안전 실행: 삭제 순서
# cf) FOREIGN_KEY_CHECKS: 외래 키 제약 조건을 활성화(1)하거나 비활성화(0)하는 명령어
SET FOREIGN_KEY_CHECKS = 0;			-- 외래 키 제약 조건 검사 OFF
DROP TABLE IF EXISTS order_logs;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS stocks;
DROP TABLE IF EXISTS products;
SET FOREIGN_KEY_CHECKS = 1 ;		-- 외래 키 제약 조건 검사 ON

-- 상품 정보 테이블
CREATE TABLE IF NOT EXISTS `products` (
	id			BIGINT AUTO_INCREMENT PRIMARY KEY,
    name 		VARCHAR(100) NOT NULL,
    price		INT NOT NULL, 
    created_at	DATETIME(6) NOT NULL,
    updated_at	DATETIME(6) NOT NULL,
    
    CONSTRAINT uq_products_name UNIQUE (name),	-- 제품명: 중복될 수 없음.
    # uq_products_name은 제약 조건의 이름(식별자)
    # 나중에 ALTER TABLE DROP CONSTRAINT uq_products_name; 같이 제약조건을 관리하기 쉬워집니다.
    
    # idx_product_name
    # name 컬럼에 인덱스를 생성합니다.
	# 인덱스가 있으면 WHERE name = '콜라' 같은 검색 속도가 빨라져요.
	# idx_products_name 은 인덱스의 이름입니다.
    
    INDEX idx_product_name (name)			# 제품명으로 제품 조회 시 성능 향상 (검색 최적화)
)	ENGINE=InnoDB							# MySQL에서 테이블이 데이터를 저장하고 관리하는 방식을 지정
    default CHARSET = utf8mb4				# DB나 테이블의 기본 문자 집합 (4바이트까지 지원 -emoji 포함)
    COLLATE = utf8mb4_unicode_ci			# 정렬 순서 지정 (대소문자 구분 없이 문자열 비교 정렬)
    COMMENT = '상품 정보';
    

# cf) ENGINE=InnoDB: 트랜잭션 지원(ACID), 외래 키 제약조건 지원(참조 무결성 보장)

-- 재고 정보 테이블
CREATE TABLE IF NOT EXISTS `stocks` (
	id			BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id 	BIGINT NOT NULL,
    quantity 	INT NOT NULL,
    created_at	DATETIME(6) NOT NULL,
    updated_at 	DATETIME(6) NOT NULL,
    
			# stocks_product 테이블을 FK로 연결
    CONSTRAINT fk_stocks_product
		FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
	CONSTRAINT chk_stocks_qty CHECK (quantity >= 0),		# CHECK 조약 조건
    INDEX idx_stocks_product_id (product_id)				# INDEX 제약  조건 
    
) 	ENGINE = InnoDB
	DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = "상품 재고 정보";

-- 주문 정보 테이블
CREATE TABLE IF NOT EXISTS `orders` (
	id 				BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id			BIGINT NOT NULL,
    order_status	VARCHAR(50) NOT NULL NOT NULL DEFAULT 'PENDING',
	created_at		DATETIME(6) NOT NULL,
    updated_at 		DATETIME(6) NOT NULL,
    
    CONSTRAINT fk_orders_user
		FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT  chk_orders_os CHECK (order_status IN ('PENDING', 'APPROVED', 'CANCELLED')),
    INDEX idx_orders_user (user_id),
    INDEX idx_orders_status (order_status),
    INDEX idx_orders_created_at (created_at)

) 	ENGINE = InnoDB
	DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = "주문 정보";

-- 주문 상세 정보 테이블
CREATE TABLE IF NOT EXISTS `order_items` (
	id			BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id  	BIGINT NOT NULL,					# 주문 정보
    product_id 	BIGINT NOT NULL,				# 제품 정보
    quantity	INT NOT NULL,
 	created_at	DATETIME(6) NOT NULL,
    updated_at 	DATETIME(6) NOT NULL,
    
    CONSTRAINT fk_order_items_order
		FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
	CONSTRAINT fk_order_items_product
		FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
	CONSTRAINT chk_order_items_qty CHECK (quantity > 0),			# CHECK 제약 조건
    INDEX idx_order_items_order (order_id),
    INDEX idx_order_items_product (product_id),
    UNIQUE KEY uq_order_product (order_id, product_id)				# 주문 정보, 제품아이디 
        
) 	ENGINE = InnoDB
	DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = "주문 상세 정보";
    
-- 주문 기록 정보 테이블
CREATE TABLE IF NOT EXISTS `order_logs` (
	id			BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id	BIGINT NOT NULL,
    message 	VARCHAR(255),
    -- 트리거가 직접 INSERT 하는 로그 테이블은 시간 누락 방지를 위해 db 기본값 유지
	created_at	DATETIME(6) NOT NULL DEFAULT current_timestamp(6),
    updated_at 	DATETIME(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_order_logs_order
		FOREIGN KEY (order_id) references orders(id) ON DELETE CASCADE,
	INDEX idx_order_logs_order (order_id), 			-- 주문 기록으로 조회
    INDEX idx_order_logs_created_at (created_at) 	-- 날짜 기준으로 조회

) 	ENGINE = InnoDB
	DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = "주문 기록 정보";

#### 초기 데이터 설정 ####

INSERT INTO products (name, price, created_at, updated_at)
VALUES
	('갤럭시 z플립7', 50000, NOW(6), NOW(6)),
	('아이폰 16', 60000, NOW(6), NOW(6)),
	('갤럭시 S25 울트라', 55000, NOW(6), NOW(6)),
	('맥북 프로 14', 80000, NOW(6), NOW(6));

INSERT INTO stocks (product_id, quantity, created_at, updated_at)
VALUES
	(1, 50, NOW(6), NOW(6)),
	(2, 30, NOW(6), NOW(6)),
	(3, 70, NOW(6), NOW(6)),
	(4, 20, NOW(6), NOW(6));
    
    
### 0902
-- 뷰는 화면에 뿌려주는 것 / 읽기 전용 테이블이다 ~ 라고 생각하기
-- 뷰 (행 단위): 주문 상세 화면 // products / orders / order_items 3개 테이블 합쳐야함.
-- : 주문 상세 화면(API) - 한 주문의 각 상품 라인 아이템 정보를 상세하게 제공 할 때
-- : ex) GET /api/v1/orders/{orderId}/items
DROP VIEW order_summary;
CREATE OR REPLACE VIEW order_summary AS
SELECT
	o.id					AS order_id,
    o.user_id				AS user_id,
    o.order_status 			AS order_status,
    p.name					AS product_name,
    oi.quantity				AS quantity,
    p.price					AS price,
    CAST((oi.quantity * p.price) AS SIGNED) AS total_price, -- BIGINT로 고정
    o.created_at			AS ordered_at
FROM
	orders o
    JOIN order_items oi ON o.id = oi.order_id	# orders와 order_items join
    JOIN products p ON oi.product_id = p.id; 	# order_items 와 products join

-- 뷰 (주문 합계)
CREATE OR REPLACE VIEW order_totals AS
SELECT
	o.id										AS order_id,
    o.user_id									AS user_id,
    o.order_status								AS order_status,
    CAST(SUM(oi.quantity * p.price) AS SIGNED) 	AS order_total_amount,
    CAST(SUM(oi.quantity) AS SIGNED)			AS order_total_quantity,
    MIN(o.created_at)							AS ordered_at			# 가장 과거의 값을 가져와라 MIN
FROM	
	orders o
    JOIN order_items oi ON o.id = oi.order_id	# orders와 order_items join
    JOIN products p ON oi.product_id = p.id

GROUP BY
	o.id, o.user_id, o.order_status;		-- 주문 별 합게: 주문(orders) 정보를 기준으로 그룹화

-- 트리거: 주문 생성 시 로그
# 고객 문의/장애 분석 시 "언제 주문 레코드가 생겼는지" 원인 추적에 사용
DELIMITER //
CREATE TRIGGER trg_after_order_insert
	AFTER INSERT ON orders
    FOR EACH ROW
    BEGIN
		INSERT INTO order_logs(order_id, message)
        VALUES (NEW.id, CONCAT('주문이 생성되었습니다. 주문 ID: ', NEW.id));
END //
DELIMITER ;

-- 트리거: 주문 상태 변경 시 로그
# 상태 전이 추적 시 "누가 언제 어떤 상태로 바꿨는지" 원인 추적에 사용
DELIMITER //
CREATE TRIGGER trg_after_order_status_update 
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
	IF NEW.order_status <> OLD.order_status THEN -- A <> B 는 A != B 와 같은 의미 (같이 않다)
		INSERT INTO order_logs(order_id, message)
        VALUES (NEW.id, CONCAT('주문 상태가 ', OLD.order_status, ' -> ', NEW.order_status, '로 변경되었습니다.'));
	END IF;
END //
DELIMITER ;

SELECT * FROM `products`;
SELECT * FROM `stocks`;
SELECT * FROM `orders`;
SELECT * FROM `order_items`;
SELECT * FROM `order_logs`;






USE k5_iot_springboot;
