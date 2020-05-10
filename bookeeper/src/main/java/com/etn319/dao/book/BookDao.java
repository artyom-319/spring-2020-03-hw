package com.etn319.dao.book;

import com.etn319.model.Author;
import com.etn319.model.Book;

import java.util.List;

public interface BookDao {
    int count();

    Book getById(long id);

    List<Book> getAll();

    Book insert(Book book);

    boolean update(Book book);

    boolean delete(Book book);

    boolean deleteById(long id);
}
