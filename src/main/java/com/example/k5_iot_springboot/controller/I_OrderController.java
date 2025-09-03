package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.common.enums.OrderStatus;
import com.example.k5_iot_springboot.dto.I_Order.request.OrderRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.OrderResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.I_OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class I_OrderController {


    // 주문 생성 / 승인 / 취소 + 검색

    private final I_OrderService orderService;

    // 주문 생성: 인증 주체의 userId 사용
    @PostMapping
    public ResponseEntity<ResponseDto<OrderResponse.Detail>> create(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody OrderRequest.OrderCreateRequest req
    ) {

        ResponseDto<OrderResponse.Detail> response = orderService.create(userPrincipal, req);
        //return ResponseEntity.ok(response);       // 단순 응답
        return ResponseEntity.ok().body(response);  // .header(), .contentType()
    }

    // 주문 승인: USER 불가, ADMIN / MANAGER o
    @PostMapping("/{orderId}/approve")
    public ResponseEntity<ResponseDto<OrderResponse.Detail>> approve(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal // 주문 승인자 정보를 저장(활용)할 경우
    ) {
        ResponseDto<OrderResponse.Detail> response = orderService.approve(orderId, userPrincipal);
        return ResponseEntity.ok(response);
    }

    // 주문 취소: USER(본인 + PENDING 한정), MANAGER, ADMIN
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ResponseDto<OrderResponse.Detail>> cancel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId
    ) {
        ResponseDto<OrderResponse.Detail> response = orderService.cancel(userPrincipal, orderId);
        return ResponseEntity.ok(response);
    }

    // 주문 검색: User(본인), MANAGER, ADMIN
    @GetMapping
    public ResponseEntity<ResponseDto<List<OrderResponse.Detail>>> search(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        ResponseDto<List<OrderResponse.Detail>> response = orderService.search(userPrincipal, userId, status, from, to);
        return ResponseEntity.ok(response);
    }
}

/*
    cf) ResponseEntity
    - HttpStatus 상태코드(200, 401.. code)
    - HttpHeaders 요청 / 응답에 대한 요구사항
    - Http 응답 본문

    ResponseDto
    - Http 응답 본문 타입(포맷) 지정 - 데이터 전송 객체
        >> result(boolean), message(String), data(T)

    // 주문 검색: userId, status, from, to 4개 필요함.
 */