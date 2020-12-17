package com.etn319.dao.mongo;

import com.etn319.model.Book;
import com.etn319.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentMongoRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByCommenter(String commenterName);
    List<Comment> findAllByBook(Book book);
}
