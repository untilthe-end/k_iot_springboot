package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.B_Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface B_StudentRepository extends JpaRepository<B_Student, Long> {
    // 이름에 특정 문자열이 포함된 학생 검색 (대소문자 구분 x)
    List<B_Student> findByNameContainingIgnoreCase(String name);
}
