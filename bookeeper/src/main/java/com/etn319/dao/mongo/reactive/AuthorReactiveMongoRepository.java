package com.etn319.dao.mongo.reactive;

import com.etn319.model.Author;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AuthorReactiveMongoRepository extends ReactiveMongoRepository<Author, String> {
    Mono<Author> findByName(String name);
}
