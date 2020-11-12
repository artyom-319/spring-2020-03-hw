package com.etn319.dao.mongo;

import com.etn319.model.mongo.MongoGenre;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenreMongoRepository extends MongoRepository<MongoGenre, String> {
}
