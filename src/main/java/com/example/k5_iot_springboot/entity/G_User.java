package com.example.k5_iot_springboot.entity;

import com.example.k5_iot_springboot.common.enums.Gender;
import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User 엔티티
 * - 테이블(users)와 1:1 매핑
 * - 생성/ 수정 시간은 BaseTimeEntity에서 자동 세팅
 * - UserDetails 책임은 분리 (별도 어댑터/매퍼가 담당)
 */
@Entity
@Table( // unique 제약 여기다가 정리하는게 깔끔하다.
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_login_id", columnNames = "login_id"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_nickname", columnNames = "nickname")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 기본 생성자 생성 - 외부 new 방지, JPA 프록시/리플렉션용
// cf) 프록시(proxy): 객체의 대리인 역할, 리플렉션(reflection): 객체의 정보를 동적으로 가져오고 조작하는 기술
// 빈 생성자로 만들어놓고 하나하나 심어줌.
public class G_User extends BaseTimeEntity {

    /** PK: 고유 번호 */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    /** 로그인 아이디 (유니크) */
    @Column(name = "login_id", updatable = false, nullable = false, length = 50)
    // 테이블명: lower_snake
    private String loginId; // 필드: lowerCamelCase

    /** 로그인 비밀번호 (해시 저장 권장 - 암호화) */
    @Column(name = "password", nullable = false, length = 255)  // 암호화 Hash 때문에 길다
    private String password;

    /** 이메일 (유니크) */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /** 닉네임 (유니크) */
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /** 성별 (선택, NULL 허용 */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    /** 생성 편의 메서드 */
    @Builder
    private G_User(String loginId, String password, String email, String nickname, Gender gender) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
    }

    // === 변경(수정) 메서드 === //
    public void changePassword(String password) {
        this.password = password;
    }

    public void changeProfile(String nickname, Gender gender) {
        this.nickname = nickname;
        this.gender = gender;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return super.getUpdatedAt();
    }
}
// @Enumerated(EnumType.STRING)
//
// 기본 동작 (정수 저장)
// JPA는 @Enumerated가 명시되지 않으면 EnumType.ORDINAL을 기본으로 사용하며,
// 이 경우 enum의 순서(ordinal 값)가 DB에 저장됩니다.
// 예를 들어 MALE이 enum의 첫 번째라면 0, FEMALE 두 번째면 1이 저장되죠.
// 하지만 이렇게 되면 enum의 순서를 바꾸거나 새 값을 추가하면,
// DB의 값과 enum 값이 어긋나는 심각한 문제가 생길 수 있습니다.
// 그래서 Enum 값을 저장할 때는
// 가급적 EnumType.STRING을 사용하는 것이 유지보수나 데이터 안정성 측면에서 추천됩니다.

/*
    @Builder란?
    : Lombok의 @Builder는 객체 생성 시 발생하는 반복적인 보일러플레이트 코드를 줄여주는 매우 유용한 도구입니다.
    이는 클래스, 생성자, 또는 메서드에 붙일 수 있고, 빌더 패턴을 자동으로 생성해줍니다

    : @Builder를 생성자 위에 붙이는 이유는
    개발자가 어떤 생성자를 통해 객체를 만드는가를 명확히 하고,
     빌더에 포함될 파라미터를 세밀하게 제어할 수 있기 때문입니다.
 */