package com.etn319.dao.mongo.reactive;

import com.etn319.model.Book;
import com.etn319.model.Comment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CommentReactiveMongoRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findAllByCommenter(String commenterName);
    Flux<Comment> findAllByBook(Book book);
    Flux<Comment> findAllByBook_Id(String id);
}
