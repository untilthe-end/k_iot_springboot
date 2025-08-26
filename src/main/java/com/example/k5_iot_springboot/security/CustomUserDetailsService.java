package com.example.k5_iot_springboot.security;

/*
    스프링 시큐리티의 DaoAuthenticationProvider가 "username"으로 사용자를 찾을 때 호출하는
        , 공식 확장 지점(UserDetailsService) 구현체

    [ 호출 흐름 ]
    1. 사용자 - 로그인 요청(username, password)
    2. UsernamePasswordAuthenticationFilter
    3. DaoAuthenticationProvider
    4. loadUserByUsername(username) ----- 해당 클래스 영역
    5. UserPrincipal 반환
    6. PasswordEncoder로 password 매칭
    7. 인증 성공 시 SecurityContext에 Authentication 저장, 이후 인가 처리 진행
 */

import com.example.k5_iot_springboot.entity.G_User;
import com.example.k5_iot_springboot.repository.G_UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final G_UserRepository userRepository;      // 데이터 접근 계층 (사용자 조회 담당)
    private final UserPrincipalMapper principalMapper;  // 변환 계층       (보안 모델로 담당)

    /*
        loadUserByUsername 메서드
        : DaoAuthenticationProvider가 username으로 사용자를 찾을 때 호출하는 메서드
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        G_User user = userRepository.findByLoginId(username)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 도메인 엔티티를 보안 VO 객체로 변환하여 반환
        return principalMapper.map(user);
    }
}
