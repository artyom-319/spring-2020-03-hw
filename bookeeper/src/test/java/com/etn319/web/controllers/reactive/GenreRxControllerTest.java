package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.GenreReactiveMongoRepositoryCustom;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.GenreDto;
import com.etn319.web.dto.mappers.BookMapper;
import com.etn319.web.dto.mappers.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GenreRxController.class)
class GenreRxControllerTest {
    private final Genre[] genres = new Genre[] {
            new Genre("Comedy"),
            new Genre("Tragedy"),
            new Genre("Novel")
    };
    private final GenreDto[] dtos = Stream.of(genres)
            .map(GenreMapper::toDto).collect(Collectors.toList()).toArray(new GenreDto[0]);
    private final Book book = new Book("Book", null, null);

    @Autowired
    private WebTestClient client;

    @MockBean
    private GenreReactiveMongoRepositoryCustom repository;
    @MockBean
    private BookReactiveMongoRepository bookRepository;

    @BeforeEach
    void setUp() {
        when(repository.findAll()).thenReturn(Flux.just(genres));
        when(bookRepository.findAllByGenreTitle(anyString())).thenReturn(Flux.just(book));
    }

    @Test
    void findAll() {
        client.get()
                .uri("/rx/api/genres")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(GenreDto.class)
                .hasSize(3)
                .contains(dtos);

    }

    @Test
    void findByGenre() {
        client.get()
                .uri("/rx/api/genres/my-genre/books")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookDto.class)
                .hasSize(1)
                .contains(BookMapper.toDto(book));
    }
}
