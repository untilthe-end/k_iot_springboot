package com.example.k5_iot_springboot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "books",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_book_writer_title", columnNames = {"writer", "title"})
        })
@Getter
@Setter
@NoArgsConstructor // @Entity 사용시 NoArgs 필수
@AllArgsConstructor
// DB CHECK와 중복됨 (필수 X, 문서화 & 이식성 작업)
@org.hibernate.annotations.Check(constraints = "category IN ('NOVEL', 'ESSAY', 'POEM', MAGAZINE')")
public class C_Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length =50) // 협업때 알기 쉬움 DB에 썼더라도 써주자.
    private String writer;

    @Column(nullable = false, length =100)
    private String title;

    @Column(nullable = false, length =500)
    private String content;

    // Enum 매핑: Converter 사용 (DB: VARCHAR) //
    // converter는 Adapter이다
    @Column(nullable = false, length =20)
    @Convert(converter = C_CategoryConverter.class)
    private C_Category category;

}
