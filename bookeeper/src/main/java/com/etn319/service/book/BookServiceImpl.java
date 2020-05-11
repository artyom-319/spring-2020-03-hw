package com.etn319.service.book;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.book.BookDao;
import com.etn319.model.Book;
import com.etn319.service.CacheHolder;
import com.etn319.service.UpdateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookDao dao;
    private final CacheHolder cache;
    private boolean created;

    @Override
    public int count() {
        return dao.count();
    }

    @Override
    public Book getById(long id) {
        try {
            var book = dao.getById(id);
            cache.setBook(book);
            return book;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<Book> getAll() {
        return dao.getAll();
    }

    @Override
    public Book save() {
        var book = cache.getBook();

        if (created) {
            Book inserted = dao.insert(book);
            clearCache();
            return inserted;
        } else {
            try {
                Book updated = dao.update(book);
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
            log.debug(e.toString());
            return false;
        }
    }

    @Override
    public List<Book> getByCachedGenre() {
        var genre = cache.getGenre();
        return dao.getByGenre(genre);
    }

    @Override
    public List<Book> getByGenreId(long id) {
        return dao.getByGenreId(id);
    }

    @Override
    public List<Book> getByCachedAuthor() {
        var author = cache.getAuthor();
        return dao.getByAuthor(author);
    }

    @Override
    public List<Book> getByAuthorId(long id) {
        return dao.getByAuthorId(id);
    }

    @Override
    public Book create(String title) {
        var book = new Book();
        book.setTitle(title);
        created = true;
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
        created = false;
        cache.clearBook();
    }

    @Override
    public Book getCache() {
        return cache.getBook();
    }
}
