package com.etn319.service.caching.impl;

import com.etn319.model.Author;
import com.etn319.service.caching.CacheHolder;
import com.etn319.service.caching.api.AuthorCachingService;
import com.etn319.service.common.api.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorCachingServiceImpl implements AuthorCachingService {
    private final AuthorService baseService;
    private final CacheHolder cache;

    @Override
    public long count() {
        return baseService.count();
    }

    @Override
    public Optional<Author> getById(String id) {
        Optional<Author> author = baseService.getById(id);
        author.ifPresent(cache::setAuthor);
        return author;
    }

    @Override
    public Optional<Author> getByName(String name) {
        Optional<Author> author = baseService.getByName(name);
        author.ifPresent(cache::setAuthor);
        return author;
    }

    @Override
    public Optional<Author> first() {
        Optional<Author> author = baseService.first();
        author.ifPresent(cache::setAuthor);
        return author;
    }

    @Override
    public List<Author> getAll() {
        return baseService.getAll();
    }

    @Override
    public Author save() {
        var author = cache.getAuthor();
        return baseService.save(author);
    }

    @Override
    public Author save(Author author) {
        return baseService.save(author);
    }

    @Override
    public void deleteById(String id) {
        baseService.deleteById(id);
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
