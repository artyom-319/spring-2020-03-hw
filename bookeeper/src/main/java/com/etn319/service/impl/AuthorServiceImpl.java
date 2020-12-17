package com.etn319.service.impl;

import com.etn319.dao.mongo.AuthorMongoRepository;
import com.etn319.model.Author;
import com.etn319.service.CacheHolder;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorMongoRepository dao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Author> getById(String id) {
        Optional<Author> author = dao.findById(id);
        author.ifPresent(cache::setAuthor);
        return author;
    }

    @Override
    public List<Author> getAll() {
        return dao.findAll();
    }

    @Override
    public Author save() {
        var author = cache.getAuthor();

        try {
            Author saved = dao.save(author);
            clearCache();
            return saved;
        } catch (DataAccessException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public void deleteById(String id) {
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
    public Author create(String name, String country) {
        var author = new Author();
        author.setName(name);
        author.setCountry(country);
        cache.setAuthor(author);
        return author;
    }

    @Override
    public Author change(String name, String country) {
        var author = cache.getAuthor();
        if (name != null)
            author.setName(name);
        if (country != null)
            author.setCountry(country);
        return author;
    }

    @Override
    public void clearCache() {
        cache.clearAuthor();
    }

    @Override
    public Author getCache() {
        return cache.getAuthor();
    }
}
