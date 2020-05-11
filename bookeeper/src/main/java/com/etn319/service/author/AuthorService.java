package com.etn319.service.author;

import com.etn319.model.Author;

import java.util.List;

public interface AuthorService {
    int count();

    Author getById(long id);

    List<Author> getAll();

    Author save();

    boolean deleteById(long id);

    Author create(String name, String country);

    Author change(String name, String country);

    void clearCache();
}
