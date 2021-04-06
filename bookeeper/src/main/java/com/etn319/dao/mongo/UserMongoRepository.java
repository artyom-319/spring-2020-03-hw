package com.etn319.dao.mongo;

import com.etn319.model.ServiceUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<ServiceUser, String>, UserMongoRepositoryCustom {
    Optional<ServiceUser> findByName(String name);
}
