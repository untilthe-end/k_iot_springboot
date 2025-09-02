package com.example.k5_iot_springboot.entity;

import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "products",
        indexes = {@Index(name = "idx_product_name", columnList = "name")},
        uniqueConstraints = {@UniqueConstraint(name = "uq_products_name", columnNames = "name")}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class I_Product extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 100)                 // @NotBlank @Size @NotNull 은 Springboot 에서 검증 하는거
    @Column(nullable = false, length = 100)    // .validation.persistence는 DB랑 매핑
    private String name;

    @NotNull                                   // 입력값 검증 시점(@Valid) - 값이 null 들어오면 Validation 에러
    @Column(nullable = false)                  // nullable - DB 테이블 생성 및 SQL 실행시 - DB에 null 저장시 DB에러
    private int price;

    @Builder
    private I_Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

/*
    @NotNull
    nullable = false
👉  둘 다 쓰는 이유는:
    애플리케이션 단에서 유효성 검사 (빠르게 피드백)
    혹시라도 검증이 누락되더라도 DB에서 최후 방어막


    @Setter 안쓰고 setName하는 이유? id값도 set안되게 하려고
 */