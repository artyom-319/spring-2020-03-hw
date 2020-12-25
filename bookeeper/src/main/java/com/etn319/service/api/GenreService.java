package com.etn319.service.api;

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
     * Загрузить в кэш жанр по названию
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

    /**
     * Создать жанр и разместить его в кэше
     * @param title название жанра
     * @return объект жанра
     */
    Genre create(String title);

    /**
     * Изменить жанр в кэше
     * @param title название жанра
     * @return объект жанра
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет жанра
     */
    Genre change(String title);

    /**
     * Очистить кэш
     */
    void clearCache();

    /**
     * Получить объект жанра из кэша
     * @return кэшированный жанр
     * @throws com.etn319.service.EmptyCacheException если в кэше нет жанра
     */
    Genre getCache();
}
