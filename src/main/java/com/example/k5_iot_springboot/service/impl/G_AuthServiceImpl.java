package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.G_Auth.request.SignInRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignUpRequest;
import com.example.k5_iot_springboot.dto.G_Auth.response.SignInResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.G_User;
import com.example.k5_iot_springboot.provider.JwtProvider;
import com.example.k5_iot_springboot.repository.G_UserRepository;
import com.example.k5_iot_springboot.service.G_AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class G_AuthServiceImpl implements G_AuthService {
    private final G_UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;                                        // ★ SpringFramework.security
    // @Bean 메서드로 BCryptPasswordEncoder 객체를 리턴
    //      , Spring Container에 등록될 때 PasswordEncoder 타입으로 인식 (주입 시 해당 타입으로 정의 권장)
    private final AuthenticationManager authenticationManager;                            // ★ SpringFramework.security
    private final JwtProvider jwtProvider;


    /** 회원 가입 */
    @Transactional
    @Override
    public void signUp(SignUpRequest req) {

        // 1) 유효성 검사 (중복 체크 - unique 제약 검증)
        // .loginId() 는 SignUpRequest는 record 라서 변수 뒤에 ()붙이면 getter 됨.
        if (userRepository.existsByLoginId(req.loginId())){
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID 입니다.");
        }
        if (userRepository.existsByEmail(req.email())){
            throw new IllegalArgumentException("이미 사용 중인 email 입니다.");
        }

        if (userRepository.existsByNickname(req.nickname())){
            throw new IllegalArgumentException("이미 사용 중인 nickname 입니다.");
        }

        // 2) 비밀번호 해시 - BCrypt 패스워드
        String encoded =  passwordEncoder.encode(req.password());

        // 3) 엔티티 생성 / 저장                       //            > @Builder 붙어있는데
        G_User user = G_User.builder()              // G_User 생성자 매개변수는 gender까지 5개인데
                                                    // 4개만 옵션으로 하고싶어서 Builder 쓴다.
                .loginId(req.loginId())
                .password(encoded)
                .email(req.email())
                .nickname(req.nickname())
                                                    // .gender(req.gender()) // null 허용 | 없어도 됨
                .build();

        userRepository.save(user);
    }

    /** 로그인 */
    @Override // 읽기 전용
    public ResponseDto<SignInResponse> signIn(SignInRequest req) {

        // ★ SpringFramework.security
        // Spring Security 표준 인증 흐름 (UserDetailsService + PasswordEncoder)

        // 로그인 id/ password 만 들어있는거 저장.
        Authentication auth = authenticationManager.authenticate(
                // 내부에서 DaoAuthenticationProvider가
                // CustomUserDetailsService.loadUserByUsername(loginId) 호출
                // >> G_User 조회
                // >> UserPrincipalMapper.map()으로 UserPrincipal 생성
                // >> PasswordEncoder로 비밀번호 비교
                // >> 인증 성공 시 Authentication 반환
                // >>>>> JWT 발급
                                                    // UserPrincipal , Credential
                new UsernamePasswordAuthenticationToken(req.loginId(), req.password())
        );

        // 2) 권한 문자열 추출 // ★ SpringFramework.security // 컬렉션 -> Stream -> 필요한 연산 -> 다시 컬렉션
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                    // GrantedAuthority.getAuthority()
                .collect(Collectors.toSet());

        // 3) JWT 발급 (username=loginId, roles 포함)
        String accessToken = jwtProvider.generateJwtToken(req.loginId(), roles);


        // 4) 만료 시각 추출하여 응답에 포함

        // C
        Claims claims = jwtProvider.getClaims(accessToken);
        long expiresAt = claims.getExpiration().getTime();

        // 5) 응답 DTO 구성
        SignInResponse response = new SignInResponse(
                "Bearer",
                accessToken,
                expiresAt,
                req.loginId(),
                roles
        );

        return ResponseDto.setSuccess("로그인 성공", response);
    }






































}
