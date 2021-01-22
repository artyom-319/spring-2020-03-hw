package com.etn319.dao.mongo.reactive;

import com.etn319.model.Genre;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenreReactiveMongoRepositoryCustom {
    Mono<Long> count();
    Flux<Genre> findAll();
    Mono<Genre> findByTitle(String title);
    Mono<Genre> first();
}
