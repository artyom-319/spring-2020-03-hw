package com.etn319.dao.mongo;

import com.etn319.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreMongoRepositoryCustom {
    long count();
    List<Genre> findAll();
    Optional<Genre> findByTitle(String title);
}
