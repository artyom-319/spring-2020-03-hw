package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.AuthorReactiveMongoRepository;
import com.etn319.dao.mongo.reactive.BookReactiveMongoRepository;
import com.etn319.web.dto.AuthorDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(AuthorRxController.class)
public class AuthorRxControllerTest {
    private AuthorDto dto = new AuthorDto();

    @Autowired
    private WebTestClient client;

    @MockBean
    private AuthorReactiveMongoRepository repository;
    @MockBean
    private BookReactiveMongoRepository bookRepository;

    @Test
    void postAuthorWithEmptyName__ShouldReturn400() {
        client.post()
                .uri("/rx/api/authors")
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
