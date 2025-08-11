package com.example.k5_iot_springboot.이론;

/*
    === 스프링 부트 계층 ===

    Client >> Controller >> Service >> Repository >> DB

    1) Controller(Presentation 계층)
        : HTTP 요청을 받음
        - 입력 DTO를 검증(@Valid)하고, business 로직은 전부 Service에 위임
        - 반환은 일관된 응답 format 으로 감싸서 반환 (ResponseEntity<ResponseDto<?>>)

    2) Service(Business 계층)
        : Transaction 경계(@Transaction)가 잡히는 곳 | 문제 생기면 Rollback
        - 도메인 규칙(중복 검사, 상태 전이, 권한 확인 등)을 처리
        - Repository를 통해 DB에 접근
        +) DTO <-> 엔티티 매핑도 발생
        +) createdAt/updatedAt은 엔티티의 @PrePersist/@PreUpdate 대신 서비스 레벨에서 관리

    3) Repository(Persistence 계층)
        : JPA를 통해 순수한 DB 접근만 담당
        - 쿼리 메서드, QueryDSL 등을 정의하고 비즈니스 로직은 절대 작성 X

    4) DB
        : 실제 데이터 영속화
        - 인덱스/제약/트랜잭션 수준 등은 무결성과 성능 향상을 담당


















 */
public class G_Constructure {
}
