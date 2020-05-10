package com.etn319.dao.author;

import com.etn319.model.Author;

import java.util.List;

public interface AuthorDao {
    int count();

    Author getById(long id);

    List<Author> getAll();

    Author insert(Author author);

    boolean update(Author author);

    boolean delete(Author author);

    boolean deleteById(long id);
}
