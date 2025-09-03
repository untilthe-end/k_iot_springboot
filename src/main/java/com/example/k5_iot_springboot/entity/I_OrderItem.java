package com.example.k5_iot_springboot.entity;

import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/*
    w주문 아이템
 */

@Entity
@Table(
        name = "order_items",
        indexes = {
                @Index(name = "idx_order_items_order", columnList = "order_id"),
                @Index(name = "idx_order_items_product", columnList = "product_id")
        },
        uniqueConstraints = {@UniqueConstraint(name = "uq_order_product", columnNames = {"order_id", "product_id"})}
)
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class I_OrderItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_items_order"))
    private I_Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_items_product"))
    private I_Product product;

    @Min(1)
    @Column(nullable = false)
    private int quantity;

    @Builder
    public I_OrderItem(I_Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    void setOrder(I_Order order) {
        this.order = order;
    }
}
/*
    주문이 있으려면 Item이 있어야 하는데
    OrderItem 에서 Product를 땡겨옴.  (얽히고 설켰네)
 */
