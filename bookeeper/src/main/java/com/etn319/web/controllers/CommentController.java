package com.etn319.web.controllers;

import com.etn319.model.Comment;
import com.etn319.service.common.api.CommentService;
import com.etn319.web.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.etn319.web.dto.mappers.CommentMapper.toDomainObject;
import static com.etn319.web.dto.mappers.CommentMapper.toDto;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @PostMapping("/api/books/{id}/comments")
    public ResponseEntity<CommentDto> newComment(@RequestBody CommentDto commentDto, @PathVariable String id) {
        Comment savedComment = service.save(toDomainObject(commentDto));
        return ResponseEntity
                .created(URI.create("/api/comments/" + savedComment.getId()))
                .body(toDto(savedComment));
    }
}
