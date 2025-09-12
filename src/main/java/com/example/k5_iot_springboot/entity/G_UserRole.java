package com.example.k5_iot_springboot.entity;

/*
    == 사용자-권한 매핑 엔티티 (user_roles) ==
 */

import com.example.k5_iot_springboot.common.enums.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "user_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class G_UserRole{

    @EmbeddedId // G_UserRoleId에 @Embeddable 되어있어서 가능
    private G_UserRoleId id;

    // 복합키의 userId를 G_User의 PK에 매핑
    @MapsId("userId")
    // 해당 필드와 연관된 FK(user_id) 값이 해당 엔티티의 식별자(PK) 중 userId 라는 필드를 채움
    // : 주로 복합키 매핑에 사용
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_roles_user")
    )
    private G_User user;

    // 복합키의 role_name을 G_Role의 PK에 매핑
    @MapsId("roleName")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "role_name",
            nullable = false,
            referencedColumnName = "role_name", // 대상 엔티티가 PK가 아닌 컬럼을 FK로 참조!
            foreignKey = @ForeignKey(name = "fk_user_roles_role")
    )
    private G_Role role;

    // user_id 가져와서 role추가
    public G_UserRole(G_User user, G_Role role){
        this.user = user;
        this.role = role;

        Long userId = user.getId();
        RoleType roleName = role.getName();
        this.id = new G_UserRoleId(userId, roleName);
    }
}

/*
    @EmbeddedId + @MapsId -> 복합키 매핑
    user_id + role_name 합쳐서 PK로 사용.
    G_UserRoleId 라는 별도 클래스(복합키 클래스)에 정의해둔걸 가져다씀

    @ManyToOne + @JoinColumn
    - user 필드는 user_id FK로 G_User 엔티티와 연결
    - role 필드는 role_name FK로 G_Role 엔티티와 연결

    생성자
    - new G_UserRole(user,role) 로 만들면 자동으로 id = new G_UserRoleId(userId, roleName) 세팅됨

    밑에 따로 있는 userId, roleName 컬럼
    - 사실 id 안에 이미 들어있지만, 편하게 직접 조회할 수 있도록 다시 선언한 것.
    - @MapsId 때문에 DB테이블에서는 중복으로 안 생기고, 그냥 읽기/쓰기용 보조 필드
 */
