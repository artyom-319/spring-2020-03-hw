package com.etn319.service.caching.impl;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import com.etn319.service.caching.CacheHolder;
import com.etn319.service.caching.api.BookCachingService;
import com.etn319.service.common.api.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookCachingServiceImpl implements BookCachingService {
    private final BookService baseService;
    private final CacheHolder cache;

    @Override
    public long count() {
        return baseService.count();
    }

    @Override
    public boolean exists(String id) {
        return baseService.exists(id);
    }

    @Override
    public Optional<Book> getById(String id) {
        Optional<Book> book = baseService.getById(id);
        book.ifPresent(cache::setBook);
        return book;
    }

    @Override
    public Optional<Book> first() {
        Optional<Book> book = baseService.first();
        book.ifPresent(cache::setBook);
        return book;
    }

    @Override
    public List<Book> getAll() {
        return baseService.getAll();
    }

    @Override
    public Book save() {
        var book = cache.getBook();
        return baseService.save(book);
    }

    @Override
    public Book save(Book book) {
        return baseService.save(book);
    }

    @Override
    public void deleteById(String id) {
        baseService.deleteById(id);
    }

    @Override
    public List<Book> getByCachedGenre() {
        var cachedGenre = cache.getGenre();
        return getByGenre(cachedGenre);
    }

    @Override
    public List<Book> getByGenre(Genre genre) {
        return baseService.getByGenre(genre);
    }

    @Override
    public List<Book> getByGenreTitle(String title) {
        return baseService.getByGenreTitle(title);
    }

    @Override
    public List<Book> getByCachedAuthor() {
        var cachedAuthor = cache.getAuthor();
        return baseService.getByAuthor(cachedAuthor);
    }

    @Override
    public List<Book> getByAuthor(Author author) {
        return baseService.getByAuthor(author);
    }

    @Override
    public List<Book> getByAuthorId(String id) {
        return baseService.getByAuthorId(id);
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
