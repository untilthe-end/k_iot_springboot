package com.example.k5_iot_springboot.dto.D_Post.response;

import com.example.k5_iot_springboot.entity.D_Post;
import com.example.k5_iot_springboot.repository.D_PostRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostWithCommentCountResponseDto(
        // 필드 선언
        Long id,
        String title,
        String author,
        Long commentCount // 댓글 개수
) {
    public static PostWithCommentCountResponseDto from(D_Post post, long commentCount) {
        if (post == null) return null;
        return new PostWithCommentCountResponseDto(
                post.getId(),
                post.getTitle(),
                post.getAuthor(),
                commentCount
        );
    }

    public static PostWithCommentCountResponseDto from(D_PostRepository.PostWithCommentCountProjection p) {
        return new PostWithCommentCountResponseDto(
          p.getPostId(),
          p.getTitle(),
          p.getAuthor(),
          p.getCommentCount()
        );
    }
}

/*
    record 는 단순히 값을 저장하고 전달 // final 불변

    record 선언 -> 생성자, 필드, 접근자 메서드 등을 자동 생성
    불변성      -> 모든 필드는 final처럼 작동, 값 변경 불가
 */