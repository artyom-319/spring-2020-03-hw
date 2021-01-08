package com.etn319.service.common.impl;

import com.etn319.dao.mongo.GenreMongoRepositoryCustom;
import com.etn319.model.Genre;
import com.etn319.service.common.api.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class GenreServiceImpl implements GenreService {
    private final GenreMongoRepositoryCustom dao;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Genre> getByTitle(String title) {
        return dao.findByTitle(title);
    }

    @Override
    public Optional<Genre> first() {
        return dao.first();
    }

    @Override
    public List<Genre> getAll() {
        return dao.findAll();
    }
}
