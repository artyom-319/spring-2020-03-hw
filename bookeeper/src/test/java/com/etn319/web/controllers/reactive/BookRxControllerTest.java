package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.CommentReactiveMongoRepository;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.CommentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebFluxTest(BookRxController.class)
public class BookRxControllerTest {
    private static final String EXISTING_BOOK_ID = "BOOK_ID";
    private static final Book BOOK = new Book(EXISTING_BOOK_ID, "title", null, null);
    private static final List<Comment> COMMENT_LIST = List.of(
            new Comment("text 1", null, null),
            new Comment("text 2", null, null),
            new Comment("text 3", null, null)
    );

    @MockBean
    private BookReactiveMongoRepository repository;
    @MockBean
    private CommentReactiveMongoRepository commentRepository;

    @Autowired
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        when(repository.findById(EXISTING_BOOK_ID)).thenReturn(Mono.just(BOOK));
        when(commentRepository.findAllByBook_Id(EXISTING_BOOK_ID))
                .thenReturn(Mono.just(COMMENT_LIST).flatMapMany(Flux::fromIterable));
    }

    @Test
    void getBookDetails__ShouldReturnBookWithComments() {
        BookDto responseBody = client.get()
                .uri("/rx/api/books/" + EXISTING_BOOK_ID)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody)
                .isNotNull()
                .extracting(BookDto::getTitle)
                .isEqualTo("title");
        List<CommentDto> comments = responseBody.getComments();
        assertThat(comments)
                .isNotEmpty()
                .extracting(CommentDto::getText)
                .containsExactly("text 1", "text 2", "text 3");
    }
}
