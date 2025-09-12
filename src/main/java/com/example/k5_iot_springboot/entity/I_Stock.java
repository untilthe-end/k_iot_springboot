package com.example.k5_iot_springboot.entity;

import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "stocks",
        indexes = {@Index(name = "idx_stocks_product_id", columnList = "product_id")} // columnList는 칼럼 product_id
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class I_Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull                        // 참조되는 값이 PK 값이기 때문에 비워질수 없다.
    @OneToOne(fetch = FetchType.LAZY, optional = false) // 제품을 출력하면서 무조건적인 재고를 출력하지 않겠다 .LAZY
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stocks_product"))
    private I_Product product;

    @Min(0)                     // 수량 0개 될수 있음, 음수 하면 Validation 에러남
    @Column(nullable = false)   // 비워질 수 있지만
    private int quantity;

    @Builder
    private I_Stock(I_Product product) {
        this.product = product;
        this.quantity = 0;              // 재고 생성 시 - 수량 초기화 (0)

    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

/*
    @OneToOne
    Stock 은 제품먼저 존재해야 있음. 1:1 관계

    optional = false 는 '연관 엔티티가 무조건 존재해야 함'을 뜻함

    JPA는 Java Persistence API 의 약자예요.
    : 스프링과 직접적인 관계는 없고, 자바에서 DB를 다루기 위한 표준 인터페이스(API) 에요.
 */