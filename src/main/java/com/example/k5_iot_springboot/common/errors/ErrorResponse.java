package com.example.k5_iot_springboot.common.errors;

import java.util.List;

// record는 생성자 자동 생성
public record ErrorResponse(
        String code, // 에러 코드
        String reason, // 사용자용 요약 메세지
        List<FieldErrorItem> errors // 검증 실패시 필드 오류 목록
) {
    public static ErrorResponse of(String code, String reason) {
        return new ErrorResponse(code, reason, List.of());
    }

    public static ErrorResponse of(String code, String reason, List<FieldErrorItem> errors) {
        return new ErrorResponse(code, reason, errors != null ? errors : List.of());
    }
}
