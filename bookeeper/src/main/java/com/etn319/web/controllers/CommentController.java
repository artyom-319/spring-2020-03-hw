package com.etn319.web.controllers;

import com.etn319.service.common.api.CommentService;
import com.etn319.web.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import static com.etn319.web.dto.mappers.CommentMapper.toDomainObject;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @PostMapping("/comments/new")
    public String newComment(CommentDto commentDto) {
        service.save(toDomainObject(commentDto));
        return "redirect:/books/" + commentDto.getBookId();
    }
}
