package com.etn319.dao.mongo.reactive;

import com.etn319.model.Author;
import com.etn319.model.Book;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BookReactiveMongoRepository extends ReactiveMongoRepository<Book, String> {
    Flux<Book> findAllByAuthor(Author author);
    Flux<Book> findAllByAuthorName(String name);
    Flux<Book> findAllByAuthor_id(String id);
    Flux<Book> findAllByGenreTitle(String title);
}
