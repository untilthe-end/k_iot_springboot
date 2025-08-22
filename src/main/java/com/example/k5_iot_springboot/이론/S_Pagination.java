package com.example.k5_iot_springboot.이론;

/*
=== 페이지네이션 Pagination ===
: 여러 개의 데이터를 페이지를 기반으로 나누어서 요청하는 방식

1. 사용 목적
- 성능/ 비용: 대용량 데이터 전체 조회 방지, DB/네트워크 부하 절감
- UX: 목록을 나누어 보여주거나 "더보기/무한스크롤" 제공

2. 페이지네이션 기본 용어
    - page: 몇 번째 페이지 (0부터 시작)
    - size: 한 페이지에 담을 데이터 수
    - sort: 정렬 기준 (Ex. createdAt & desc - 최신순)
    - totalElements/totalPages: 전체 개수/전체 페이지 수 (Offset 방식에서만 사용)
    - Page VS Slice
        >> Page: 전체 개수(count) 포함 ("총 몇 페이지인지" 같은 정보가 필요할 때)
        >> Slice: 전체 개수 없이 "다음 페이지가 더 있는지(hasNext)"만 판단
                (성능 유리 - 무한 스크롤/더보기에 적합)

3. 페이지네이션 종류 (2가지)
    1) Offset 기반(page/size/정렬)
        - 요청: /boards?page=0&size=10&sort=createdAt,desc
        - 장점: 직관적, "총 페이지 수" 안내 가능
        - 단점: 뒤 페이지로 갈수록 count(*) 비용 증가 (대용량에서 부담)
        >> 관리자 목록, 정확한 "총 건수/총 페이지 수"가 필요한 화면
            , 페이지 점프가 필요한 UI << < 1 2 3 4 5 > >> 이런거

        +) 페이지 번호(page)와 크기(size)로 건너뛸 개수(offset = page x size)를 계싼하여
            LIMIT size OFFSET offset

    2) Cursor(Keyset) 기반(cursorId/size)
        - 요청: /boards/cursor?cursorId=마지막아이템id&size=10
        - 장점: 빠름(대용량 유리), 인피니트 스크롤에 최적
        - 단점: "총 페이지 수" 계산 어려움, 정렬을 특정 키로 고정 (id 또는 createdAt + id) -최신순/인기순 못함
        >> 데이터가 클 경우 (수십만 ~ 수천만 건), 무한 스크롤/피드형 목록, 실시간성의 성능이 중요한 화면

        +) 마지막으로 본 키 이후 데이터를 키 조건(id < listId)로 조회
            앞부분을 통째로 건너뛰지 않음
            >> 정렬 키가 반드시 유니크/일관되어야 함

 4. 페이지네이션 API 설계
    1) OffSet 기반(일반 목록)
        - EndPoint: GET /boards
        - Query: page, size(기본 10개, 최대 100개 권장), sort
        - 응답: constent(목록) + meta(페이지 정보) 구조

    2) Cursor 기반(무한스크롤/더보기)
        - EndPoint: GET /boards/cursor
        - Query: cursorId(처음 호출 시 생략 - 최신부터), size(기본 10개, 최대 100개 권장)
        - 응답: content(목록) + hasNext + nextCursor (다음 요청에 바로 사용)

    ex) 20개 들고와서 최신순/인기순 누르기만 하면 바뀌는거는 Front-End
 */
public class S_Pagination {

}
