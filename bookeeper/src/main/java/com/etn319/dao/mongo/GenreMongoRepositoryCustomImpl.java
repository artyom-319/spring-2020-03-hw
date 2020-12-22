package com.etn319.dao.mongo;

import com.etn319.model.Book;
import com.etn319.model.Genre;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Repository
@RequiredArgsConstructor
public class GenreMongoRepositoryCustomImpl implements GenreMongoRepositoryCustom {
    private final MongoOperations template;

    @Override
    public long count() {
        Aggregation aggregation = newAggregation(
                project().andExclude("_id").and("genre.title").as("title"),
                match(Criteria.where("title").ne(null)),
                group("_id", "title"),
                Aggregation.count().as("longValue")
        );
        LongAggregationHolder result = template
                .aggregate(aggregation, Book.class, LongAggregationHolder.class)
                .getUniqueMappedResult();
        return result == null ? 0 : result.getLongValue();
    }

    @Override
    public List<Genre> findAll() {
        Aggregation aggregation = newAggregation(
                project().andExclude("_id").and("genre.title").as("title"),
                match(Criteria.where("title").ne(null)),
                group("_id", "title")
        );
        return template.aggregate(aggregation, Book.class, Genre.class).getMappedResults();
    }

    @Override
    public Optional<Genre> findByTitle(String title) {
        Aggregation aggregation = newAggregation(
                project().andExclude("_id").and("genre.title").as("title"),
                match(Criteria.where("title").is(title))
        );
        return Optional.ofNullable(
                template.aggregate(aggregation, Book.class, Genre.class).getUniqueMappedResult());
    }

    @Data
    public static class LongAggregationHolder {
        private long longValue;
    }
}
