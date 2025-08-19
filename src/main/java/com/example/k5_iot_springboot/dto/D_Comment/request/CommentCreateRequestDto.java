package com.example.k5_iot_springboot.dto.D_Comment.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
// JSON 데이터를 Java 객체로 매핑할 때,
// 객체에 정의되지 않은(=모르는) 속성들이 있어도 무시하고 넘어가라는 설정입니다.
// JSON에 불필요하거나 알 수 없는 필드가 있어도 에러 없이 무시해라
public record CommentCreateRequestDto (
        @NotBlank(message = "내용은 필수 입력 값입니다.")
        @Size(max = 1_000, message = "내용은 최대 1,000자 까지 입력 가능합니다.")
        String content,

        @NotBlank(message = "작성자는 필수 입력 값입니다.")
        @Size(max = 100, message = "작성자는 최대 100자까지 입력 가능합니다.")
        String commenter
){}
