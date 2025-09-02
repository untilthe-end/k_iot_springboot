package com.example.k5_iot_springboot.dto.I_Order.request;

public class StockRequest {
    // 재고 증가 / 감소 요청 DTO
    public record StockAdjust (
            Long productId,
            int delta       // 변화량을 뜻함
    ) {}

    // 재고 직접 설정 요청 DTO
    public record StockSet (
            Long productId,
            int quantity
    ) {}
}
