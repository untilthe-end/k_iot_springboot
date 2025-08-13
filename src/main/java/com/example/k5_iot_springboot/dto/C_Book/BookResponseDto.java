package com.example.k5_iot_springboot.dto.C_Book;

import com.example.k5_iot_springboot.entity.C_Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // 모든 필드를 한 번에 초기화할 수 있는 생성자를 자동 생성, 간편하게 new
public class BookResponseDto {
        private String writer;
        private String title;
        private C_Category category;
}
