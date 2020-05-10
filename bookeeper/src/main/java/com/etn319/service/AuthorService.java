package com.etn319.service;

import com.etn319.dao.author.AuthorDao;
import com.etn319.model.Author;
import com.etn319.shell.CacheHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService { /*
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

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Author save() {
        var author = cache.getAuthor();
        if (author == null)
            throw new EmptyCacheException("author");
        if (created) {
            Author inserted = dao.insert(author);
            created = false;
            cache.setAuthor(null);
            return inserted;
        } else {
            boolean isSaved = dao.save(author);
            if (isSaved) {
                cache.setAuthor(null);
                return author;
            } else
                throw new UpdateException();
        }
    }

    @Override
    public boolean delete(long id) {
        return dao.deleteById(id);
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
    */
}
