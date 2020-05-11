package com.etn319.service.genre;

import com.etn319.model.Genre;

import java.util.List;

public interface GenreService {
    int count();

    Genre getById(long id);

    List<Genre> getAll();

    Genre save();

    boolean deleteById(long id);

    Genre create(String title);

    Genre change(String title);

    void clearCache();
}
