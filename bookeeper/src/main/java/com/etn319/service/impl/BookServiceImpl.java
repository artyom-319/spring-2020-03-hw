package com.etn319.service.impl;

import com.etn319.dao.mongo.BookMongoRepository;
import com.etn319.model.Book;
import com.etn319.service.CacheHolder;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookMongoRepository dao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Book> getById(String id) {
        Optional<Book> book = dao.findById(id);
        book.ifPresent(cache::setBook);
        return book;
    }

    @Override
    public List<Book> getAll() {
        return dao.findAll();
    }

    @Override
    public Book save() {
        var book = cache.getBook();
        try {
            Book saved = dao.save(book);
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
    public List<Book> getByCachedGenre() {
        var cachedGenre = cache.getGenre();
        return getByGenreTitle(cachedGenre.getTitle());
    }

    @Override
    public List<Book> getByGenreTitle(String title) {
        return dao.findAllByGenreTitle(title);
    }

    @Override
    public List<Book> getByCachedAuthor() {
        var cachedAuthor = cache.getAuthor();
        return getByAuthorId(cachedAuthor.getId());
    }

    @Override
    public List<Book> getByAuthorId(String id) {
        return dao.findAllByAuthor_id(id);
    }

    @Override
    public Book create(String title) {
        var book = new Book();
        book.setTitle(title);
        cache.setBook(book);
        return book;
    }

    @Override
    public Book change(String title) {
        var book = cache.getBook();
        book.setTitle(title);
        return book;
    }

    @Override
    public Book wireAuthor() {
        var book = cache.getBook();
        var author = cache.getAuthor();
        book.setAuthor(author);
        return book;
    }

    @Override
    public Book wireGenre() {
        var book = cache.getBook();
        var genre = cache.getGenre();
        book.setGenre(genre);
        return book;
    }

    @Override
    public void clearCache() {
        cache.clearBook();
    }

    @Override
    public Book getCache() {
        return cache.getBook();
    }
}
