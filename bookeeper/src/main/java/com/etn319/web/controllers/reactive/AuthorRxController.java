package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.AuthorReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.model.Author;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.AuthorDto;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.mappers.AuthorMapper;
import com.etn319.web.dto.mappers.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.etn319.web.dto.mappers.AuthorMapper.toDomainObject;
import static com.etn319.web.dto.mappers.AuthorMapper.toDto;

@RestController
@RequiredArgsConstructor
public class AuthorRxController {
    private final AuthorReactiveMongoRepository repository;
    private final BookReactiveMongoRepository bookRepository;

    @GetMapping("/api/authors")
    public Flux<AuthorDto> list() {
        return repository
                .findAll()
                .map(AuthorMapper::toDto);
    }

    @GetMapping("/api/authors/{id}")
    public Mono<AuthorDto> details(@PathVariable("id") String id) {
        return repository
                .findById(id)
                .map(AuthorMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Author not found: id=" + id));
    }

    @GetMapping("/api/authors/{id}/books")
    public Flux<BookDto> listBooksOfAuthor(@PathVariable("id") String id) {
        return bookRepository
                .findAllByAuthor_id(id)
                .map(BookMapper::toDto);
    }

    @PostMapping("/api/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthorDto> newAuthor(@RequestBody AuthorDto authorDto) {
        return Mono.just(authorDto)
                .map(AuthorMapper::toDomainObject)
                // todo: exceptions
                .flatMap(repository::save)
                .map(AuthorMapper::toDto);
    }

    @PutMapping("/api/authors")
    public ResponseEntity<AuthorDto> updateAuthor(@RequestBody AuthorDto authorDto) {
        if (!service.exists(authorDto.getId())) {
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
