package com.etn319.web.controllers;

import com.etn319.model.Author;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.service.common.api.AuthorService;
import com.etn319.service.common.api.BookService;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.AuthorDto;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.mappers.AuthorMapper;
import com.etn319.web.dto.mappers.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.etn319.web.dto.mappers.AuthorMapper.toDomainObject;
import static com.etn319.web.dto.mappers.AuthorMapper.toDto;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService service;
    private final BookService bookService;

    @GetMapping("/api/authors")
    public List<AuthorDto> list() {
        return service.getAll()
                .stream()
                .map(AuthorMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/authors/{id}")
    public AuthorDto details(@PathVariable("id") String id) {
        return service.getById(id)
                .map(AuthorMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Author not found: id=" + id));
    }

    @GetMapping("/api/authors/{id}/books")
    public List<BookDto> listBooksOfAuthor(@PathVariable("id") String id) {
        return bookService.getByAuthorId(id)
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/api/authors")
    public ResponseEntity<AuthorDto> newAuthor(@RequestBody AuthorDto authorDto) {
        Author savedAuthor = service.save(toDomainObject(authorDto));
        return ResponseEntity
                .created(URI.create("/authors"))
                .body(toDto(savedAuthor));
    }

    @PutMapping("/api/authors")
    public ResponseEntity<AuthorDto> updateAuthor(@RequestBody AuthorDto authorDto) {
        // todo: exists в сервисах
        if (service.getById(authorDto.getId()).isEmpty()) {
            throw new NotFoundException("Author id=" + authorDto.getId() + " does not exist");
        }
        Author savedAuthor = service.save(toDomainObject(authorDto));
        return ResponseEntity
                .ok()
                .body(toDto(savedAuthor));
    }

    @DeleteMapping("/api/authors/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EmptyMandatoryFieldException.class)
    public ResponseEntity<String> emptyMandatoryFieldHandler(Throwable t) {
        return ResponseEntity
                .badRequest()
                .body(t.getMessage());
    }
}
