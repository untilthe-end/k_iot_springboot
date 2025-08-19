package com.example.k5_iot_springboot.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Entity
@Table(name ="comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "post")

public class D_Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // java
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // - Comment : Post = N:1 관계에서 'N'쪽 매핑임을 설정
    // - Lazy 설정으로 필요할 때만 게시글을 로딩
    // - optional = false: FK가 반드시 존재해야 함을 보장 (데이터 무결성)
    //      >> 게시글 없이 댓글 존재 할 수 없다.

    // table
    // - 외래키 컬럼명 지정
    // - NO NULL 제약 조건 부여: FK 설정
    @JoinColumn(name = "post_id", nullable = false)
    private D_Post post; // D_Post에 mappedBy ="post" 와 연결됨

    @Comment("댓글 내용")
    @Column(nullable = false, length = 1000)
    private String content;

    @Comment("댓글 작성자 표시명 또는 ID")
    @Column(nullable = false, length = 100)
    private String commenter;

    // === 생성/ 수정 메서드 === //
    private D_Comment(String content, String commenter) {
        this.content = content;
        this.commenter = commenter;
    }

    public static D_Comment create(String content, String commenter) {
        return new D_Comment(content, commenter);
    }

    // 접근제한자가 default (같은 패키지 내에서)
    // POST에서만 댓글이 세팅되도록 가시성 축소(연관관계 일관성 유지)
    void setPost(D_Post post) {
        this.post = post;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
