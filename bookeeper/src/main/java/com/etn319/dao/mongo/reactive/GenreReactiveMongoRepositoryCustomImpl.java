package com.etn319.dao.mongo.reactive;

import com.etn319.model.Book;
import com.etn319.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Repository
@RequiredArgsConstructor
public class GenreReactiveMongoRepositoryCustomImpl implements GenreReactiveMongoRepositoryCustom {
    private final ReactiveMongoOperations template;

    @Override
    public Mono<Long> count() {
        Aggregation aggregation = newAggregation(
                project().andExclude("_id").and("genre.title").as("title"),
                match(Criteria.where("title").ne(null)),
                group("_id", "title"),
                Aggregation.count().as("longValue")
        );
        return template
                .aggregate(aggregation, Book.class, LongAggregationHolder.class)
                .single()
                .defaultIfEmpty(new LongAggregationHolder(0L))
                .map(LongAggregationHolder::getLongValue);
    }

    @Override
    public Flux<Genre> findAll() {
        Aggregation aggregation = newAggregation(
                project().andExclude("_id").and("genre.title").as("title"),
                match(Criteria.where("title").ne(null)),
                group("_id", "title")
        );
        return template.aggregate(aggregation, Book.class, Genre.class);
    }

    @Override
    public Mono<Genre> findByTitle(String title) {
        Aggregation aggregation = newAggregation(
                project().andExclude("_id").and("genre.title").as("title"),
                match(Criteria.where("title").is(title))
        );
        return template
                .aggregate(aggregation, Book.class, Genre.class)
                .singleOrEmpty();
    }

    @Override
    public Mono<Genre> first() {
        Aggregation aggregation = newAggregation(
                project().andExclude("_id").and("genre.title").as("title"),
                Aggregation.limit(1)
        );
        return template
                .aggregate(aggregation, Book.class, Genre.class)
                .singleOrEmpty();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LongAggregationHolder {
        private long longValue;
    }
}
