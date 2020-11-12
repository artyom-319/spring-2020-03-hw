package com.etn319.dao.mongo;

import com.etn319.model.mongo.MongoAuthor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthorMongoRepository extends MongoRepository<MongoAuthor, String> {
    Optional<MongoAuthor> findByName(String name);
}
