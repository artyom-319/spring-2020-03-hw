package com.etn319.service.impl;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.api.GenreDao;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.ServiceLayerException;
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
    private final GenreDao dao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Genre> getById(long id) {
        Optional<Genre> genre = dao.getById(id);
        genre.ifPresent(cache::setGenre);
        return genre;
    }

    @Override
    public List<Genre> getAll() {
        return dao.getAll();
    }

    @Override
    public Genre save() {
        var genre = cache.getGenre();
        try {
            Genre saved = dao.save(genre);
            clearCache();
            return saved;
        } catch (DaoLayerException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            dao.deleteById(id);
        } catch (DaoLayerException e) {
            throw new ServiceLayerException(e);
        }
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
