package com.etn319.service;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component
public class CacheHolder {
    private Author author;
    private Genre genre;
    private Book book;

    public Author getAuthor() {
        if (author == null)
            throw new EmptyCacheException("Author");
        return author;
    }

    public Genre getGenre() {
        if (genre == null)
            throw new EmptyCacheException("Genre");
        return genre;
    }

    public Book getBook() {
        if (book == null)
            throw new EmptyCacheException("Book");
        return book;
    }

    public void clearAuthor() {
        this.author = null;
    }

    public void clearGenre() {
        this.genre = null;
    }

    public void clearBook() {
        this.book = null;
    }
}
