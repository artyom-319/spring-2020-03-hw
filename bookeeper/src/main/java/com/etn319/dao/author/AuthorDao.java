package com.etn319.dao.author;

import com.etn319.model.Author;

import java.util.List;

public interface AuthorDao {
    int count();

    Author getById(long id);

    List<Author> getAll();

    Author insert(Author author);

    Author update(Author author);

    void delete(Author author);

    void deleteById(long id);
}
