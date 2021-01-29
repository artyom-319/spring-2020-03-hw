package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.CommentReactiveMongoRepository;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.mappers.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import static com.etn319.web.dto.mappers.BookMapper.toDomainObject;

@RestController
@RequestMapping("/rx")
@RequiredArgsConstructor
public class BookRxController {
    private final BookReactiveMongoRepository repository;
    private final CommentReactiveMongoRepository commentRepository;

    @GetMapping("/api/books")
    public Flux<BookDto> list() {
        return repository
                .findAll()
                .map(BookMapper::toDto);
    }

    @GetMapping("/api/books/{id}")
    public Mono<BookDto> details(@PathVariable("id") String id) {
        return repository
                .findById(id)
                .zipWith(commentRepository.findAllByBook_Id(id).collectList(), (book, comments) -> {
                    book.setComments(comments);
                    return book;
                })
                .map(BookMapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Book not found: id=" + id)));
    }

    @PostMapping("/api/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> newBook(@RequestBody BookDto bookDto) {
        return Mono.just(bookDto)
                .filter(book -> isNotNullOrBlank(book.getTitle()))
                .switchIfEmpty(Mono.error(new EmptyMandatoryFieldException("Book title cannot be empty")))
                .map(BookMapper::toDomainObject)
                .flatMap(repository::save)
                .then(repository.findById(bookDto.getId()))
                .map(BookMapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Book not found: id=" + bookDto.getId())));
    }

    @PutMapping("/api/books")
    public Mono<BookDto> updateBook(@RequestBody BookDto bookDto) {
        return Mono.just(bookDto)
                .filter(book -> isNotNullOrBlank(book.getTitle()))
                .switchIfEmpty(Mono.error(new EmptyMandatoryFieldException("Book title cannot be empty")))
                .flatMap(dto -> repository.existsById(dto.getId()))
                .zipWhen(exists -> exists ?
                        repository.save(toDomainObject(bookDto))
                        : Mono.error(new NotFoundException("Book id=" + bookDto.getId() + " does not exist"))
                )
                .map(Tuple2::getT2)
                .then(repository.findById(bookDto.getId()))
                .map(BookMapper::toDto);
    }

    @DeleteMapping("/api/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable("id") String bookId) {
        return repository
                .existsById(bookId)
                .zipWhen(exists -> exists ?
                        repository.deleteById(bookId)
                        : Mono.error(new NotFoundException("Book id=" + bookId + " does not exist")))
                .map(Tuple2::getT2)
                .onErrorMap(MappingException.class, ServiceLayerException::new);
    }

    private boolean isNotNullOrBlank(String source) {
        return source != null && !source.trim().isBlank();
    }
}
