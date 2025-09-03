package com.example.k5_iot_springboot.config;

import com.example.k5_iot_springboot.filter.JwtAuthenticationFilter;
import com.example.k5_iot_springboot.handler.JsonAccessDeniedHandler;
import com.example.k5_iot_springboot.handler.JsonAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * === WebSecurityConfig ===
 * : Spring Security를 통해 웹 애플리케이션의 보안을 구성 (보안 환경설정)
 * - (세션 대신) JWT 필터를 적용하여 인증 처리, CORS 및 CSRF 설정을 비활성화
 * >> 서버 간의 통신을 원활하게 처리
 * - URL 별 접근 권한, 필터 순서 (JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 배치) 등
 * <p>
 * # Stateless(무상태성, 세션 미사용) + CSRF 비활성 (JWT 를 쓰면되니까)
 */

@Configuration                   // 해당 클래스가 Spring의 설정 클래스로 사용됨을 명시
@EnableWebSecurity               // Spring Security의 웹 보안 활성화 - Spring Security 기능을 웹 계층에 적용
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 사용자 정의 JWT 검증 필터 (아래에서 필터 체인에 추가)
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    // CORS 관련 속성을 properties에서 주입받아 콤마(,)로 분리하여 저장하는 데이터
    @Value("${cors.allowed-origins:*}") // https://app.example.com, https://admin.example.com
    private String allowedOrigins;

    @Value("${cors.allowed-headers:*")
    private String allowedHeaders;

    @Value("${cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String allowedMethod;

    @Value("${cors.exposed-headers:Authorization,Set-Cookie")
    private String exposedHeader; // 필요한 헤더만 노출

    @Value("${security.h2-console:true}") // 로컬 개발 시 true - 개발용 h2 콘솔 접근 허용 여부 (아래에서 권한 부여)
    private boolean h2ConsoleEnabled;     // 배포할때는 false

    /**
     * ============================
     * PasswordEncoder / AuthManager
     * ============================
     */

    /**
     * 비밀번호 인코더: 실무 기본 BCrypt (강도 기본값)
     */
    @Bean // 메서드 반환 객체를 스프링 빈으로 등록
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();            // 날것, 암호화 된 것
        // >> 추후 회원가입/로그인 시 passwordEncoder.matches(raw, encoded); 비밀번호 비교
    }

    /**
     * Spring이 구성한 것(AuthenticationManager)을 노출 - 스프링 기본 구성 재사용
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        // 표준 인증 흐름(UserDetailsService 등)을 사용할 수 있음
        return configuration.getAuthenticationManager();
    }

    /**
     * ==========================
     * CORS
     * <p>
     * cf) CORS (Cross-Origin Resource Sharing)
     * : 브라우저(EX. 4178)에서 다른 도메인(Tomcat 서버: 8080)으로부터 리소스를 요청할 때 발생하는 보안 정책
     * - REST API 사용 시 다른 출처(도메인)에서 API에 접근할 수 있도록 허용하는 정책
     * <p>
     * corsConfigurationSource 메서드
     * : 특정 출처에서 온 HTTP 요청을 허용하거나 거부할 수 있는 필터
     * ==========================
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();    // 출처/헤더/메서드/쿠키 허용 등을 담는 CORS 정책 객체

        List<String> origins = splitToList(allowedOrigins);

        config.setAllowCredentials(true);                      // 1) 인증 정보(쿠키/Authorization) 허용
//        config.setAllowedOriginPatterns(origins);            // 2) Origin 설정 - 도메인 매칭  | 반드시 써야하지만 =* 했기 때문에 (배포시 사용)
        //          >> 허용 origin을 *로 둘 수 없음 (반드시 구체적인 도메인이어야 함)
        config.setAllowedHeaders(splitToList(allowedHeaders)); // 3) 요청 헤더 화이트 리스트
        config.setAllowedMethods(splitToList(allowedMethod));  // 4) 허용 메서드
        config.setExposedHeaders(splitToList(exposedHeader));  // 5) 응답에서 클라이언트가 읽을 수 있는 헤더 (Authorization/ cookie)두개

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // ** 전체를 의미 / 모든 경로에 동일 CORS 정책 적용
        return source;
    }

    /**
     * ==========================
     * Security Filter Chain
     * : 보안 필터 체인 정의, 특정 HTTP 요청에 대한 웹 기반 보안 구성
     * - CSRF 보호를 비활성화, CORS 정책을 활성화
     * ==========================
     */ // JWT가 CSRF 를 보호하기때문에.. 그렇단다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) CSRF 비활성 (JWT + REST 조합에서 일반적)
                .csrf(AbstractHttpConfigurer::disable)

                // 2) 세션 미사용 (완전 무상태 - 모든 요청은 토큰만으로 인증/인가 진행))
                .sessionManagement(sm
                        -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3) CORS 활성화 (위의 Bean 적용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 예외 처리 지점 (필요시 커스텀 핸들러 연결)
                // : 폼 로그인/HTTP Basic 비활성 - JWT만 사용!
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        // H2 DB 콘솔 = 브라우저로 접속 가능한 H2 데이터베이스 관리 도구
        // H2 Database 콘솔은 웹 브라우저에 iframe 태그를 사용하여 페이지를 띄움.
        // : 로컬 개발 환경에서 H2 콘솔을 보려면 해당 설정 필요
        if (h2ConsoleEnabled) {
            http.headers(headers
                    -> headers.frameOptions(frame -> frame.sameOrigin()));
            // -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        }

        // 5) URL 인가 규칙
        http
                .authorizeHttpRequests(auth -> {
                    // H2 콘솔 접근 권한 열기 (개발 환경에서 DB를 직접 확인 - 인증 절차 없이 접속할 수 있도록 예외)
                    if (h2ConsoleEnabled) auth.requestMatchers("/h2-console/**").permitAll();

                    // SecurityFilterChain URL 보안 규칙
                    auth
                            // PreFlight 허용
                            // 브라우저가 실제 요청을 보내기 전에,
                            // 먼저 "이 요청을 보내도 안전한지?" 서버에 미리 물어보는 과정을 말합니다.
                            //즉, 본 요청 전에 사전 확인하는 HTTP 요청이에요.
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                            // === URL 레벨에서 1차 차단 (+ 컨트롤러 메서드에서 @PreAuthorize로 2차 방어) === //
                            // 인증/회원가입 등 공개 엔드포인트 - 토큰이 필요없는 기능
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/api/v1/articles/**").permitAll()

                            // 마이페이지(내 정보) - 인증 필요 (모든 역할 가능)
                            .requestMatchers("/api/v1/users/me/**").authenticated() // 인증이 필요하다 keyword

                            // boards 접근 제어
                            .requestMatchers(HttpMethod.GET,      "/api/v1/boards/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                            .requestMatchers(HttpMethod.POST,     "/api/v1/boards/**").hasAnyRole("MANAGER", "ADMIN")
                            .requestMatchers(HttpMethod.PUT,      "/api/v1/boards/**").hasAnyRole("MANAGER", "ADMIN")
                            .requestMatchers(HttpMethod.DELETE,   "/api/v1/boards/**").hasAnyRole("ADMIN")

                            // articles 접근 제어
                            .requestMatchers(HttpMethod.GET, "/api/v1/articles/**").permitAll()

                            // ADMIN 전용 권용 관리 API
                            .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")

                            // products 접근 제어
                            .requestMatchers(HttpMethod.GET,    "/api/v1/products/**").permitAll()      // 이거 안넣으면 제품 조회시에도 토큰 필요하다고 함.
                            .requestMatchers(HttpMethod.POST,     "/api/v1/products/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT,     "/api/v1/products/**").hasRole("ADMIN")

                            // stocks 접근 제어
                            .requestMatchers(HttpMethod.GET,    "/api/v1/stocks/**").permitAll()      // 이거 안넣으면 제품 조회시에도 토큰 필요하다고 함.
                            .requestMatchers(HttpMethod.POST,     "/api/v1/stocks/**").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT,     "/api/v1/stocks/**").hasAnyRole("ADMIN", "MANAGER")

                            // orders 접근 제어

                            // 읽기 공개 예시 (게시글 목록, 조회 등)
                            .anyRequest().authenticated(); // 나머지는 인증 필요 - JWT 토큰이 있어야 접근 가능
                    }
                );

        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 문자열(콤마 구분)을 리스트로 변환
    private static List<String> splitToList(String csv) {
        return Arrays.stream(csv.split(","))    // ["사과", " 바나나", " 배", " "]
                .map(String::trim)                    // ["사과", "바나나", "배", ""]
                .filter(s -> !s.isBlank())      // ["사과", "바나나", "배"]
                .toList();                            // List.of("사과", "바나나", "배")
    }
}
