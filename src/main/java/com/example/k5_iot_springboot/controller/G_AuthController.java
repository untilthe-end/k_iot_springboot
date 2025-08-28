package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.dto.G_Auth.request.SignInRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignUpRequest;
import com.example.k5_iot_springboot.dto.G_Auth.response.SignInResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.service.G_AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth") // 회원가입, 로그인, 아이디 찾기, 비밀번호 재설정 등 - 토큰 필요없는..
@RequiredArgsConstructor // 의존성 주입 따라온다. final
public class G_AuthController {
    private final G_AuthService authService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto<Void>> signUp(@Valid @RequestBody SignUpRequest req) {
        authService.signUp(req);
        return ResponseEntity.ok(ResponseDto.setSuccess("회원가입이 완료되었습니다. ", null));
    }

    // 로그인
    @PostMapping("/sign-in")
    public ResponseEntity<ResponseDto<SignInResponse>> signIn(@Valid @RequestBody SignInRequest req) {
        ResponseDto<SignInResponse> response = authService.signIn(req);
        return ResponseEntity.ok().body(response);
    }
}

/*
    ResponseEntity
    : 응답 본문(body) + HTTP 상태 코드(status) + 헤더(headers)
    -> 위에 ResponseEntity.ok().body(response) 는 200 OK + body만 줌 -> 헤더는 없음.
 */