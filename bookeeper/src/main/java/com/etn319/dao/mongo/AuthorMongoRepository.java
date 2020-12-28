package com.etn319.dao.mongo;

import com.etn319.model.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// todo: запилить миграции
public interface AuthorMongoRepository extends MongoRepository<Author, String> {
    Optional<Author> findByName(String name);
}
