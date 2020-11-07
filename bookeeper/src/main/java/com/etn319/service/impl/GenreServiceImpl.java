package com.etn319.service.impl;

import com.etn319.dao.GenreRepository;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository dao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Genre> getById(long id) {
        Optional<Genre> genre = dao.findById(id);
        genre.ifPresent(cache::setGenre);
        return genre;
    }

    @Override
    public List<Genre> getAll() {
        return dao.findAll();
    }

    @Override
    @Transactional
    public Genre save() {
        var genre = cache.getGenre();
        try {
            Genre saved = dao.save(genre);
            clearCache();
            return saved;
        } catch (DataAccessException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        if (!dao.existsById(id)) {
            throw new EntityNotFoundException();
        }

        try {
            dao.deleteById(id);
        } catch (DataAccessException e) {
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
