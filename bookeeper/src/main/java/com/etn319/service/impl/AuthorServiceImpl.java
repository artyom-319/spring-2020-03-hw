package com.etn319.service.impl;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.api.AuthorDao;
import com.etn319.model.Author;
import com.etn319.service.CacheHolder;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorDao dao;
    private final CacheHolder cache;

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return dao.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> getById(long id) {
        Optional<Author> author = dao.getById(id);
        author.ifPresent(cache::setAuthor);
        return author;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> getAll() {
        return dao.getAll();
    }

    @Override
    @Transactional
    public Author save() {
        var author = cache.getAuthor();

        try {
            Author saved = dao.save(author);
            clearCache();
            return saved;
        } catch (DaoLayerException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        try {
            dao.deleteById(id);
        } catch (DaoLayerException e) {
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
