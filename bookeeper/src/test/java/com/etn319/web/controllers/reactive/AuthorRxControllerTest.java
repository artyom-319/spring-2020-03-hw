package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.AuthorReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.web.dto.AuthorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthorRxController.class)
public class AuthorRxControllerTest {
    private static final String EXISTING_ID = "EXISTING_ID";

    @Autowired
    private WebTestClient client;

    @MockBean
    private AuthorReactiveMongoRepository repository;
    @MockBean
    private BookReactiveMongoRepository bookRepository;

    @BeforeEach
    void setUp() {
        when(repository.existsById(anyString())).thenReturn(Mono.just(false));
        when(repository.existsById(eq(EXISTING_ID))).thenReturn(Mono.just(true));
        when(repository.deleteById(eq(EXISTING_ID))).thenReturn(Mono.empty());
    }

    @Test
    void postAuthorWithEmptyName__ShouldReturn400() {
        var dto = new AuthorDto();
        client.post()
                .uri("/rx/api/authors")
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Author name cannot be empty");
    }

    @Test
    void putAuthorWithEmptyName__ShouldReturn400() {
        var dto = new AuthorDto();
        dto.setId(EXISTING_ID);
        client.put()
                .uri("/rx/api/authors")
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Author name cannot be empty");
    }

    @Test
    void putAuthorThatDoesNotExist__ShouldReturn400() {
        var dto = new AuthorDto("invalid_id", "name", "country");
        client.put()
                .uri("/rx/api/authors")
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Author id=" + dto.getId() + " does not exist");
    }

    @Test
    void deleteAuthorThatDoesNotExist__ShouldReturn400() {
        var invalidId = "invalid_id";
        client.delete()
                .uri("/rx/api/authors/" + invalidId)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Author id=" + invalidId + " does not exist");
    }

    @Test
    void deleteExistingAuthor__ShouldReturn204() {
        client.delete()
                .uri("/rx/api/authors/" + EXISTING_ID)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
