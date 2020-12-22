package com.etn319.service.impl;

import com.etn319.dao.mongo.GenreMongoRepositoryCustom;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.api.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreMongoRepositoryCustom dao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Genre> getByTitle(String title) {
        Optional<Genre> genre = dao.findByTitle(title);
        genre.ifPresent(cache::setGenre);
        return genre;
    }

    @Override
    public Optional<Genre> first() {
        Optional<Genre> genre = dao.first();
        genre.ifPresent(cache::setGenre);
        return genre;
    }

    @Override
    public List<Genre> getAll() {
        return dao.findAll();
    }

    @Override
    public Genre create(String title) {
        var genre = new Genre();
        genre.setTitle(title);
        cache.setGenre(genre);
        return genre;
    }

    @Override
    public Genre change(String title) {
        var genre = cache.getGenre();
        genre.setTitle(title);
        return genre;
    }

    @Override
    public void clearCache() {
        cache.clearGenre();
    }

    @Override
    public Genre getCache() {
        return cache.getGenre();
    }
}
