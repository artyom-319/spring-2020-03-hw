package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.GenreReactiveMongoRepositoryCustom;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.GenreDto;
import com.etn319.web.dto.mappers.BookMapper;
import com.etn319.web.dto.mappers.GenreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController("/rx")
@RequiredArgsConstructor
public class GenreRxController {
    private final GenreReactiveMongoRepositoryCustom repository;
    private final BookReactiveMongoRepository bookRepository;

    @GetMapping("/api/genres")
    public Flux<GenreDto> genreList() {
        return repository
                .findAll()
                .map(GenreMapper::toDto);
    }

    @GetMapping("/api/genres/{title}/books")
    public Flux<BookDto> booksByGenre(@PathVariable("title") String title) {
        return bookRepository
                .findAllByGenreTitle(title)
                .map(BookMapper::toDto);
    }

    @ExceptionHandler(EmptyMandatoryFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> emptyMandatoryFieldHandler(Throwable t) {
        return Mono.just(t)
                .map(Throwable::getMessage);
    }
}
