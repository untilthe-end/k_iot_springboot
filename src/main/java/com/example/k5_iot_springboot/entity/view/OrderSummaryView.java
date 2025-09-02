package com.example.k5_iot_springboot.entity.view;

/*
    order_summary 뷰 매핑용 릭기 전용 엔티티
    : 조인 결과(행 단위) 제공
    - 리포트/목록에 활용
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_summary")
@Getter
@NoArgsConstructor
@Immutable                      // 읽기 전용 이기 때문에
public class OrderSummaryView {
    @Id @Column(name = "order_id")
    private Long orderId;

    private Long user_id;           // 뷰 컬럼명 그대로 사용
    private String order_status;    // 문자열 컬럼 (필요 시 enum 변환은 서비스 에서!)
    private String product_name;
    private Integer quantity;
    private Integer price;
    private Long total_price;

    @Column(name = "ordered_at")
    private LocalDateTime orderAt;

}
