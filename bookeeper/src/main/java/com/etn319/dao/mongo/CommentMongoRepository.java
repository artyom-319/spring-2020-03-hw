package com.etn319.dao.mongo;

import com.etn319.model.mongo.MongoComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentMongoRepository extends MongoRepository<MongoComment, String> {
    List<MongoComment> findAllByCommenter(String commenterName);
}
