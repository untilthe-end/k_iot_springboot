package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.entity.view.Notice;
import com.example.k5_iot_springboot.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CrossOrigin 은 (테스트용으로만 써라)
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
// final 또는 @NonNull 붙은 필드를 자동으로 생성자 주입해줌.
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping
    public Notice create(@RequestBody Notice req){
        Notice result = noticeService.create(req.getTitle(), req.getContent(), req.getAuthor());
        return result;
    }

    @GetMapping
    public List<Notice> getAll() {
        List<Notice> result = noticeService.getAll();
        return result;
    }

    @GetMapping("/{id}")
    public Notice getById(@PathVariable Long id) {
        Notice result = noticeService.getById(id);
        return result;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        noticeService.delete(id);
    }
}
