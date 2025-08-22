package com.example.k5_iot_springboot.common.errors;

// [ 자동 제공 ]

// private final - record는 불변 값
// @AllArgsConstructor
// Getter
// toString(), equals(), hashCode()
public record FieldErrorItem (
        String field,
        String rejected,
        String message
){ }
