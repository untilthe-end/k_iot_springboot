package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.common.constants.ApiMappingPattern;
import com.example.k5_iot_springboot.dto.F_Board.request.BoardRequestDto;
import com.example.k5_iot_springboot.dto.F_Board.response.BoardResponseDto;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.service.F_BoardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// @Controller → 이 클래스가 웹 요청을 처리하는 컨트롤러임을 표시
//
// @ResponseBody → 메서드의 리턴값을 뷰(JSP, Thymeleaf 등)로 렌더링하지 않고, HTTP 응답 Body에 그대로 담아서 보냄
//👉 그래서 @RestController를 붙이면 메서드의 반환 객체가 자동으로 JSON 으로 직렬화(Serialize)되어 클라이언트에 전송됩니다.

@RequestMapping(ApiMappingPattern.Boards.ROOT)
@RequiredArgsConstructor
@Validated
// 검증 기능 활성화 @Valid 와 같이 동작함

public class F_BoardController {

    private final F_BoardService boardService;

    // 1) 게시글 생성
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping
    // ResponseEntity: 응답 상태 코드와 body 제어
    public ResponseEntity<ResponseDto<BoardResponseDto.DetailResponse>> createBoard(
            @Valid @RequestBody BoardRequestDto.CreateRequest request
            // @RequestBody: 클라이언트가 보낸 JSON을 BoardRequestDto.CreateRequest 객체로 매핑
    ) {

        ResponseDto<BoardResponseDto.DetailResponse> response = boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        //boardService에 request 전달 → 비즈니스 로직 실행 (DB 저장 등)
        //응답 데이터를 ResponseDto로 감싸서 반환

    }


    // 2) 게시글 조회 (전체 조회)
    @PreAuthorize("hasAnyRole('USER','MANAGER', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<BoardResponseDto.SummaryResponse>>> getAllBoards() {
        ResponseDto<List<BoardResponseDto.SummaryResponse>> response = boardService.getAllBoards();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 2-1) 게시글 조회 (페이지네이션 OffSet 조회)
    @PreAuthorize("hasAnyRole('USER','MANAGER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseDto<BoardResponseDto.PageResponse>> getBoardsPage(
            // page: 0부터 시작, 필요 시 1부터 시작하는 정책도 가능
            @RequestParam(defaultValue = "0") @Min(0) int page,
            // size: 최대 100 제한 (과도한 요청 방지)
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            // sort: 여러 개 허용 - EX) sort=createAt,desc&sort=title,asc
            @RequestParam(required = false) String[] sort
    ) {
        ResponseDto<BoardResponseDto.PageResponse> response = boardService.getBoardsPage(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 2-2) 게시글 조회 (페이지네이션 Cursor 조회)
    @PreAuthorize("hasAnyRole('USER','MANAGER', 'ADMIN')")
    @GetMapping("/cursor")
    public ResponseEntity<ResponseDto<BoardResponseDto.SliceResponse>> getBoardsByCursor(
            // 처음 요청이면 null >> 가장 최신부터 시작
            // : 목록을 항상 하나의 정렬 기준으로 고정! (id DESC - 최신 글 먼저)
            // > 다음 페이지를 가져올 때는 기준 커서보다 더 오래된 (작은 id) 행만 가져오기
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        ResponseDto<BoardResponseDto.SliceResponse> response = boardService.getBoardsByCursor(cursorId, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 3) 게시글 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping(ApiMappingPattern.Boards.ID_ONLY)
    public ResponseEntity<ResponseDto<BoardResponseDto.DetailResponse>> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDto.UpdateRequest request
    ){
        ResponseDto<BoardResponseDto.DetailResponse> response = boardService.updateBoard(boardId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 4) 게시글 삭제
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping(ApiMappingPattern.Boards.ID_ONLY)
}











