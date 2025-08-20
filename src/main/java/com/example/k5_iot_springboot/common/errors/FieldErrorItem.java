package com.example.k5_iot_springboot.common.errors;

public record FieldErrorItem (
        String field,
        String rejected,
        String message
){ }
