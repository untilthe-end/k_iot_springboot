package com.example.k5_iot_springboot.entity.base;

/*
    가장 중요함!!
    생성/ 수정 시간을 자동으로 관리하는 공통 부모 클래스

    - 모든 엔티티에서 해당 클래스를 상속하면 createdAt/updatedAt이 자동 세팅
    - 필드는 LocalDateTime 사용, DB는 DATETIME(6)으로 저장(타임존 정보 없음)
    - UTC 기준으로 저장되도록 애플리케이션 기본 타임존은 UTC로 고정(TimeConfig)
 */

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
// schema 모양 원통 생김
// 실제 테이블 만들지 않고, 필드를 자식 엔티티 컬럼에 포함시킴(@Entity 하지 않아서 mysql에 테이블 안 만들어도됨)


@EntityListeners(AuditingEntityListener.class)
// Auditing 이벤트 리스너 활성화
// Spring Data JPA가 생성/수정 이벤트를 감지해서 @CreatedDate, @LastModifiedDate 값을 넣어줍니다.


// 공통으로 쓰일 "시간 기록용 부모 클래스"
// abstract class → 추상 클래스라서 단독으로 객체 생성 불가
// 다른 엔티티(F_Board 등)가 extends BaseTimeEntity 해서 재사용함
// DATETIME(6) 이란?  '2025-08-22 11:29:31.098879' 초단위를 소수점 6자리까지 가능!!세팅
public abstract class BaseTimeEntity {

    // 레코드 최초 생성 시 자동 세팅되는 시간(UTC 기준)

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    // 레코드가 수정될 때 마다 자동 갱신되는 시간(UTC 기준)

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;
}
