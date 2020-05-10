package com.etn319.dao.genre;

import com.etn319.model.Genre;

import java.util.List;

public interface GenreDao {
    int count();

    Genre getById(long id);

    List<Genre> getAll();

    Genre insert(Genre genre);

    Genre update(Genre genre);

    void delete(Genre genre);

    void deleteById(long id);
}
