package com.etn319.service.impl;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.api.AuthorDao;
import com.etn319.dao.api.BookDao;
import com.etn319.dao.api.GenreDao;
import com.etn319.model.Book;
import com.etn319.service.CacheHolder;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookDao dao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Book> getById(long id) {
        Optional<Book> book = dao.getById(id);
        book.ifPresent(cache::setBook);
        return book;
    }

    @Override
    public List<Book> getAll() {
        return dao.getAll();
    }

    @Override
    @Transactional
    public Book save() {
        var book = cache.getBook();
        try {
            Book saved = dao.save(book);
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
    @Transactional(readOnly = true)
    public List<Book> getByCachedGenre() {
        var cachedGenre = cache.getGenre();
        return getByGenreId(cachedGenre.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByGenreId(long id) {
        var genre = genreDao.getById(id)
                .orElseThrow(() -> new ServiceLayerException("Genre does not exist"));
        return new ArrayList<>(genre.getBooks());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByCachedAuthor() {
        var cachedAuthor = cache.getAuthor();
        return getByAuthorId(cachedAuthor.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByAuthorId(long id) {
        var author = authorDao.getById(id)
                .orElseThrow(() -> new ServiceLayerException("Author does not exist"));
        return new ArrayList<>(author.getBooks());
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
