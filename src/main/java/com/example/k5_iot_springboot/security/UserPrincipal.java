package com.example.k5_iot_springboot.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/*
  === UserPrincipal ===
  : "보안 관점에서 사용자 표현"을 담당하는 값 객체(Value Object, VO) -- 실제 값 createdAt/updatedAt은 있으면 안됨.
  - Spring Security가 인증/인가 과정에서 인지하는 최소한의 사용자 정보 집합
  - 엔티티(G_User) 자체를 그대로 쓰지 않고, 인증에 필요한 값만 안전하게 전달/보관
       >> 결합도 낮춤, 캐시/직렬화 안정성 ↑

  필요성
  1) Security의 표준 진입점: AuthenticationProvider는 UserDetails 타입을 통해 사용자 정보와 권한을 검사
       >> CustomUserDetailsSErvice#loadUserByUername()에서 DB 사용자 정보를 읽고
         UserPrincipal로 감싼 뒤 반환하면, 이후 인증 과정이 표준화되어 동작

  2) 경량화/안정성 ↑: 만일, 영속성 엔티티(G_User)를 SecurityContext에 보관하면
                      , 직렬화 문제, 지연로딩, 순환 참조 등의 문제 발생 가능성 증가
                      >> VO 형태의 UserPrincipal은 인증에 필요한 최소 데이터만 포함하고 있어서 안전하다!

    >> 인증 성공 시 Authentication(principal)에 들어가 SecurityContextHolder에 저장됨
      - Controller 에서 @AuthenticationPrincipal,  UserPrincipal principal로 주입받아 사용
      - JWT 토큰 만들 때 클레임(claim)으로 넣는 정보도 UserPrincipal 에서 가져옴

    >> 권한 모델 (authorities): GrantedAuthority 집합
        Ex) new SimpleGrantedAuthority("ROLE_USER")
        - "ROLE_" 접두어를 붙이는 것을 권장!
        - 스프링 시큐리티의 HasRole("USER") / hasAuthority("ROLE_USER") 검사와 호환

    >> 설계 포인트
   1) 불변성 유지: 모든 필드는 final로 만들어 변경 불가
   2) 보안 강화  : @JsonIgnore, @ToString(exclude="password")를 통해 비밀번호 유출을 2중 차단
   3) 빌더 사용  : 가독성 ↑, 테스트 용이

*/

/** In conclusion,,,
 *  DB 객체(G_User)는 무겁고 위험 -> 인증용으로 바로 쓰면 문제 발생 가능
 *  UserPrincipal은 "보안용 최소 정보만 담은 가벼운 사용자 객체"
 *  인증, JWT 발급, 권한 체크 등 모든 보안 과정에서 안전하게 사용 가능
 */
@Getter
@ToString(exclude = "password") // 로그 등에 password가 노출되지 않도록 제외
public class UserPrincipal implements UserDetails {
    // UserDetails: Security가 요구하는 사용자 정보 interface
    //              >> Spring Security가 사용자의 정보를 불러오기 위해서는 UserDetails를 구현해야 함!

    private final Long id;                                              // PK
    private final String username;                                      // 로그인 아이디
    @JsonIgnore
    private final String password;                                      // 해시 비밀번호
    private final Collection<? extends GrantedAuthority> authorities;   // 권한

    /* 계정 상태 플래그들

      각 데이터가 false면 만료 - 로그인 거부

      EX) 도메인 정책 예시
           1. 비밀번호 5회 실패 - accountNonLocked=false
           2. 장기 미접속 휴면 - enabled=false
           3. 주기적 비밀번호 변경 - credentialsNonExpired=false
     */
    private final boolean accountNonExpired;                            // 계정 만료 여부
    private final boolean accountNonLocked;                             // 계정 잠금 여부
    private final boolean credentialsNonExpired;                        // 비밀번호(자격) 만료 여부
    private final boolean enabled;                                      // 활성화 여부

    /* 생성자: 불변 객체 생성을 위한 Builder 기반 생성자

      - 서비스/어댑터 계층에서 엔티티 정보를 읽고, 필요한 정보만 골라 UserPrincipal로 변환하여 반환
    */
    @Builder
    private UserPrincipal(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            boolean enabled
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    // === UserDetails 인터페이스 구현 ===
    /*
      Spring Security가 AuthenticationProvider 및 AccessDecisionManager를 통해
       인증/인가 수행 시 아래의 메서드 사용

      >> 값 반환 이외의 로직 X
    */
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    // : 계정의 권한 목록 리턴
    @Override public String getPassword() { return password; }
    // : 계정의 비밀번호(해시값) 리턴
    // >> 인증 단계에서 DaoAuthenticationProvider가 비밀번호 매칭에 사용
    @Override public String getUsername() { return username; }
    // : 계정의 고유한 값을 리턴!!
    // >> DB PK 값, 중복이 없는 unique 값
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }
    // : 계정의 만료 여부 리턴
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    // : 계정의 잠김 여부 리턴
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    // : 비밀번호 만료 여부 리턴
    @Override public boolean isEnabled() { return enabled; }
}