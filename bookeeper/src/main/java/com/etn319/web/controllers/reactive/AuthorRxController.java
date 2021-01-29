package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.AuthorReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.AuthorDto;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.mappers.AuthorMapper;
import com.etn319.web.dto.mappers.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static com.etn319.web.dto.mappers.AuthorMapper.toDomainObject;

@RestController
@RequestMapping("/rx")
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
                .switchIfEmpty(Mono.error(new NotFoundException("Author not found: id=" + id)));
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
                .filter(dto -> isNotNullOrBlank(dto.getName()))
                .switchIfEmpty(Mono.error(new EmptyMandatoryFieldException("Author name cannot be empty")))
                .map(AuthorMapper::toDomainObject)
                .flatMap(repository::save)
                .onErrorMap(MappingException.class, ServiceLayerException::new)
                .map(AuthorMapper::toDto);
    }

    @PutMapping("/api/authors")
    public Mono<AuthorDto> updateAuthor(@RequestBody AuthorDto authorDto) {
        return Mono.just(authorDto)
                .filter(dto -> isNotNullOrBlank(dto.getName()))
                .switchIfEmpty(Mono.error(new EmptyMandatoryFieldException("Author name cannot be empty")))
                .flatMap(dto -> repository.existsById(dto.getId()))
                .zipWhen(b -> b ?
                        repository.save(toDomainObject(authorDto))
                        : Mono.error(new NotFoundException("Author id=" + authorDto.getId() + " does not exist")))
                .map(Tuple2::getT2)
                .map(AuthorMapper::toDto)
                .onErrorMap(MappingException.class, ServiceLayerException::new);
    }

    @DeleteMapping("/api/authors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return repository.deleteById(id);
    }

    @ExceptionHandler(EmptyMandatoryFieldException.class)
    public Mono<String> emptyMandatoryFieldHandler(Throwable t) {
        return Mono.just(t)
                .map(Throwable::getMessage);
    }

    private boolean isNotNullOrBlank(String source) {
        return source != null && !source.trim().isBlank();
    }
}
