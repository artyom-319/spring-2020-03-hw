package com.etn319.dao.book;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;

import java.util.List;

public interface BookDao {
    int count();

    Book getById(long id);

    List<Book> getAll();

    Book insert(Book book);

    Book update(Book book);

    void delete(Book book);

    void deleteById(long id);

    List<Book> getByGenre(Genre genre);

    List<Book> getByGenreId(long genreId);

    List<Book> getByAuthor(Author author);

    List<Book> getByAuthorId(long authorId);
}
