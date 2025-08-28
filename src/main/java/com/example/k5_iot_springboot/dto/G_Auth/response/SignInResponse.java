package com.example.k5_iot_springboot.dto.G_Auth.response;

import java.util.Set;

public record SignInResponse (
        String tokenType,       // "Bearer"
        String accessToken,     // JWT
        long expiresAt,         // 만료 시각 (ms)
        String username,        // loginId
        Set<String> roles       // ["ROLE_USER"]
){
}

// 불변 final / getter / toString / 생성자 자동 생성