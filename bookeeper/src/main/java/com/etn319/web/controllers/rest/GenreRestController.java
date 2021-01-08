package com.etn319.web.controllers.rest;

import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.service.common.api.BookService;
import com.etn319.service.common.api.GenreService;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.GenreDto;
import com.etn319.web.dto.mappers.BookMapper;
import com.etn319.web.dto.mappers.GenreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class GenreRestController {
    private final GenreService service;
    private final BookService bookService;

    @GetMapping("/api/genres")
    public List<GenreDto> genreList() {
        return service.getAll()
                .stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/genres/{title}/books")
    public List<BookDto> booksByGenre(@PathVariable("title") String title) {
        return bookService.getByGenreTitle(title)
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(EmptyMandatoryFieldException.class)
    public ResponseEntity<String> emptyMandatoryFieldHandler(Throwable t) {
        return ResponseEntity
                .badRequest()
                .body(t.getMessage());
    }
}
