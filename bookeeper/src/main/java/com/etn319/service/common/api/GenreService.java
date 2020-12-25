package com.etn319.service.common.api;

import com.etn319.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    /**
     * Количество жанров
     * @return количество жанров
     */
    long count();

    /**
     * Поиск жанра по названию
     * @param title название искомого жанра
     * @return объект жанра, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Genre> getByTitle(String title);

    /**
     * Извлечь из базы случайный жанр
     * @return объект жанра, упакованный в <code>Optional</code>, если в базе есть хоть один жанр
     * <code>Optional.empty()</code>, если база пустая
     */
    Optional<Genre> first();

    /**
     * Получить все жанры
     * @return список жанров
     */
    List<Genre> getAll();
}
