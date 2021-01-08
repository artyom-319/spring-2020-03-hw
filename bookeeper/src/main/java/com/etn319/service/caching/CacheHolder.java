package com.etn319.service.caching;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.model.Genre;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component
public class CacheHolder {
    private Author author;
    private Genre genre;
    private Book book;
    private Comment comment;

    public Author getAuthor() {
        if (author == null)
            throw new EmptyCacheException("author");
        return author;
    }

    public Genre getGenre() {
        if (genre == null)
            throw new EmptyCacheException("genre");
        return genre;
    }

    public Book getBook() {
        if (book == null)
            throw new EmptyCacheException("book");
        return book;
    }

    public Comment getComment() {
        if (comment == null)
            throw new EmptyCacheException("comment");
        return comment;
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

    public void clearComment() {
        this.comment = null;
    }
}
