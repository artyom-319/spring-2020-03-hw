package com.etn319.service.author;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.author.AuthorDao;
import com.etn319.model.Author;
import com.etn319.service.CacheHolder;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.UpdateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorDao dao;
    private final CacheHolder cache;
    private boolean created;

    @Override
    public int count() {
        return dao.count();
    }

    @Override
    public Author getById(long id) {
        try {
            Author author = dao.getById(id);
            cache.setAuthor(author);
            return author;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<Author> getAll() {
        return dao.getAll();
    }

    @Override
    public Author save() {
        var author = cache.getAuthor();

        if (author == null)
            throw new EmptyCacheException("author");

        if (created) {
            Author inserted = dao.insert(author);
            created = false;
            clearCache();
            return inserted;
        } else {
            try {
                Author updated = dao.update(author);
                clearCache();
                return updated;
            } catch (EntityNotFoundException e) {
                throw new UpdateException(e);
            }
        }
    }

    @Override
    public boolean deleteById(long id) {
        try {
            dao.deleteById(id);
            return true;
        } catch (RuntimeException e) {
            log.info(e.toString());
            return false;
        }
    }

    @Override
    public Author create(String name, String country) {
        var author = new Author();
        author.setName(name);
        author.setCountry(country);
        cache.setAuthor(author);
        created = true;
        return author;
    }

    @Override
    public Author change(String name, String country) {
        var author = cache.getAuthor();
        if (author == null)
            throw new EmptyCacheException("Author");
        if (name != null)
            author.setName(name);
        if (country != null)
            author.setCountry(country);
        return author;
    }

    @Override
    public void clearCache() {
        created = false;
        cache.clearAuthor();
    }

    @Override
    public Author getCache() {
        return cache.getAuthor();
    }
}
