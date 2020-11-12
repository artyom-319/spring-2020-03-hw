package com.etn319.dao.mongo;

import com.etn319.model.mongo.MongoAuthor;
import com.etn319.model.mongo.MongoBook;
import com.etn319.model.mongo.MongoGenre;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookMongoRepository extends MongoRepository<MongoBook, String> {
    List<MongoBook> findAllByAuthor(MongoAuthor author);
    List<MongoBook> findAllByGenre(MongoGenre genre);
}
