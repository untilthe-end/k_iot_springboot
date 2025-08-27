package com.example.k5_iot_springboot.dto.G_Admin.request;

import com.example.k5_iot_springboot.common.enums.RoleType;
import jakarta.validation.constraints.NotNull;

public record RoleModifyRequest(
        @NotNull      // 문자열이 아니여서 notBlank 못씀
        RoleType role // 추가, 삭제 대상으로 사용할 역할
) {
}
