package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.common.constants.ApiMappingPattern;
import com.example.k5_iot_springboot.dto.D_Comment.request.CommentCreateRequestDto;
import com.example.k5_iot_springboot.dto.D_Comment.request.CommentUpdateRequestDto;
import com.example.k5_iot_springboot.dto.D_Comment.response.CommentResponseDto;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.service.D_CommentService;
import jakarta.servlet.http.HttpServlet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiMappingPattern.Comments.ROOT)
@RequiredArgsConstructor
@Validated
public class D_CommentController {
    private final D_CommentService commentService;
    private final HttpServlet httpServlet;

    // -- 댓글 조회는 없는 이유는, 게시글에서 불러오는 거라서.

    // [POST] /api/v1/posts/{postId}/comments
    // 1) 댓글 생성
    @PostMapping
    public ResponseEntity<ResponseDto<CommentResponseDto>> createComment(
            @PathVariable("postId") @Positive(message = "postId는 1 이상의 정수여야 합니다.") Long postId,
            @Valid @RequestBody CommentCreateRequestDto dto
    )  {
        ResponseDto<CommentResponseDto> response = commentService.createComment(postId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // [PUT] /api/v1/posts/{postId}/comments/{commentId}
    // 2) 댓글 수정
    @PutMapping(ApiMappingPattern.Comments.ID_ONLY)
    public ResponseEntity<ResponseDto<CommentResponseDto>> updateComment(
            @PathVariable("postId") @Positive(message = "postId는 1 이상의 정수여야 합니다.") Long postId,
            @PathVariable("commentId") @Positive(message = "commentId는 1 이상의 정수여야 합니다.") Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto dto
            ){
        ResponseDto<CommentResponseDto> response = commentService.updateComment(postId, commentId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 3) 댓글 삭제
    @DeleteMapping(ApiMappingPattern.Comments.ID_ONLY)
    public ResponseEntity<ResponseDto<CommentResponseDto>> deleteComment(
            @PathVariable("postId") @Positive(message = "postId는 1 이상의 정수여야 합니다.") Long postId,
            @PathVariable("commentId") @Positive(message = "commentId는 1 이상의 정수여야 합니다.") Long commentId
    ) {
        ResponseDto<CommentResponseDto> response = commentService.deleteComment(postId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
