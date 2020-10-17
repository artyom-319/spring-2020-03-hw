package com.etn319.service.genre;

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
     * Загрузить в кэш жанр по id
     * @param id идентификатор искомого жанра
     * @return объект жанра, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Genre> getById(long id);

    /**
     * Получить все жанры
     * @return список жанров
     */
    List<Genre> getAll();

    /**
     * Сохраняет кэшированный жанр. В случае успеха очищает кэш
     * @return сохранённый объект жанра
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет жанра
     */
    Genre save();

    /**
     * Удаляет жанр по id
     * @param id id жанра, который нужно удалить
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при удалении
     */
    void deleteById(long id);

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
