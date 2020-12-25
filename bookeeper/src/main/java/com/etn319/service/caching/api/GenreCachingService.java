package com.etn319.service.caching.api;

import com.etn319.model.Genre;
import com.etn319.service.caching.EmptyCacheException;
import com.etn319.service.common.api.GenreService;

public interface GenreCachingService extends GenreService {
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
     * @throws EmptyCacheException, если в кэше нет жанра
     */
    Genre change(String title);

    /**
     * Очистить кэш
     */
    void clearCache();

    /**
     * Получить объект жанра из кэша
     * @return кэшированный жанр
     * @throws EmptyCacheException если в кэше нет жанра
     */
    Genre getCache();
}
