package com.example.k5_iot_springboot.common.errors;

import java.util.List;

public record ErrorResponse(
        String code, // 에러 코드
        String reason, // 사용자용 요약 메세지
        List<FieldErrorItem> errors // 검증 실패시 필드 오류 목록
) {
    public static ErrorResponse of(String code, String reason) {
        return new ErrorResponse(code, reason, List.of());
        /* record 클래스로 정의하면 모든 필드값을 포함한 생성자가 자동으로 생겨.

        그래서 new ErrorResponse(안에는 3개의 매개변수가 필요하지)
                            List.of() 가 뭐냐 하면 불변 리스트 생성 메서드야
                            errors가 null 일 경우에도 절대 null이 되지 않게 [] 빈 배열 을 주지.
        */
    }

    public static ErrorResponse of(String code, String reason, List<FieldErrorItem> errors) {
        return new ErrorResponse(code, reason, errors != null ? errors : List.of());
    }
}
