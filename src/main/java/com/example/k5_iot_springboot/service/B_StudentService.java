package com.example.k5_iot_springboot.service;

import com.example.k5_iot_springboot.dto.B_student.StudentCreateRequestDto;
import com.example.k5_iot_springboot.dto.B_student.StudentResponseDto;
import com.example.k5_iot_springboot.dto.B_student.StudentUpdateRequestDto;

import java.util.List;

public interface B_StudentService {
    StudentResponseDto createStudent(StudentCreateRequestDto requestDto);

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto updateStudent(Long id, StudentUpdateRequestDto requestDto);

    void deleteStudent(Long id);

    List<StudentResponseDto> filterStudentsByName(String name);
}
