package com.etn319.web.controllers;

import com.etn319.service.common.api.CommentService;
import com.etn319.web.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import static com.etn319.web.dto.mappers.CommentMapper.toDomainObject;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @PostMapping("/comments/new")
    public String newComment(CommentDto commentDto) {
        log.info("POST /comments/new received for book {}", commentDto.getBookId());
        service.save(toDomainObject(commentDto));
        return "redirect:/books/" + commentDto.getBookId();
    }
}
