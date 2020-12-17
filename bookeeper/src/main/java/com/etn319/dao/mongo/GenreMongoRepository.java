package com.etn319.dao.mongo;

import com.etn319.model.Genre;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenreMongoRepository extends MongoRepository<Genre, String> {
}
