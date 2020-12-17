package com.etn319.dao.mongo;

import com.etn319.model.Author;
import com.etn319.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookMongoRepository extends MongoRepository<Book, String> {
    List<Book> findAllByAuthor(Author author);
    List<Book> findAllByAuthorName(String name);
    List<Book> findAllByAuthor__id(String id);
    List<Book> findAllByGenreTitle(String title);
}
