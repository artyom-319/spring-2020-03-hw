package com.etn319.web.controllers;

import com.etn319.model.Book;
import com.etn319.service.common.api.BookService;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.CommentDto;
import com.etn319.web.dto.mappers.BookMapper;
import com.etn319.web.dto.mappers.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.etn319.web.dto.mappers.BookMapper.toDomainObject;
import static com.etn319.web.dto.mappers.BookMapper.toDto;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class BookController {
    private final BookService service;

    @GetMapping("/api/books")
    public List<BookDto> list() {
        return service.getAll()
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/books/{id}")
    public BookDto list(@PathVariable("id") String id) {
        Book bookDomain = service.getById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: id=" + id));
        List<CommentDto> comments = bookDomain.getComments()
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        BookDto book = toDto(bookDomain);
        book.setComments(comments);
        return book;
    }

    @PostMapping("/api/books")
    public BookDto newBook(@RequestBody BookDto bookDto) {
        Book savedBook = service.save(toDomainObject(bookDto));
        return toDto(savedBook);
    }

    @PutMapping("/api/books")
    public ResponseEntity<BookDto> updateBook(@RequestBody BookDto bookDto) {
        if (!service.exists(bookDto.getId())) {
            throw new NotFoundException("Book id=" + bookDto.getId() + " does not exist");
        }
        Book savedBook = service.save(toDomainObject(bookDto));
        return ResponseEntity
                .ok()
                .body(toDto(savedBook));
    }

    @DeleteMapping("/api/books/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable("id") String bookId) {
        service.deleteById(bookId);
        return ResponseEntity.noContent().build();
    }
}
