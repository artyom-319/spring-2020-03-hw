package com.etn319.service.book;

import com.etn319.model.Book;

import java.util.List;

public interface BookService {
    int count();

    Book getById(long id);

    List<Book> getAll();

    Book save();

    boolean deleteById(long id);

    Book create(String title);

    Book change(String title);

    Book wireAuthor();

    Book wireGenre();
}
