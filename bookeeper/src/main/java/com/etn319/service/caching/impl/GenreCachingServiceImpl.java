package com.etn319.service.caching.impl;

import com.etn319.model.Genre;
import com.etn319.service.caching.CacheHolder;
import com.etn319.service.caching.api.GenreCachingService;
import com.etn319.service.common.api.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreCachingServiceImpl implements GenreCachingService {
    private final GenreService baseService;
    private final CacheHolder cache;

    @Override
    public long count() {
        return baseService.count();
    }

    @Override
    public Optional<Genre> getByTitle(String title) {
        Optional<Genre> genre = baseService.getByTitle(title);
        genre.ifPresent(cache::setGenre);
        return genre;
    }

    @Override
    public Optional<Genre> first() {
        Optional<Genre> genre = baseService.first();
        genre.ifPresent(cache::setGenre);
        return genre;
    }

    @Override
    public List<Genre> getAll() {
        return baseService.getAll();
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
