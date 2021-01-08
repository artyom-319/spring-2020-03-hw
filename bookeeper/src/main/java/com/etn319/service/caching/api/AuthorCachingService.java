package com.etn319.service.caching.api;

import com.etn319.model.Author;
import com.etn319.service.caching.EmptyCacheException;
import com.etn319.service.common.api.AuthorService;

public interface AuthorCachingService extends AuthorService {
    /**
     * Создать автора и разместить его в кэше
     * @param name имя автора
     * @param country страна проживания автора
     * @return объект автора
     */
    Author create(String name, String country);

    /**
     * Сохраняет кэшированного автора. В случае успеха очищает кэш
     * @return сохранённый объект автора
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     * @throws EmptyCacheException, если в кэше нет автора
     */
    Author save();

    /**
     * Изменить автора в кэше. Если какой-либо из параметров {@code null}, то соответствующее поле
     * автора не меняется
     * @param name имя автора
     * @param country страна проживания автора
     * @return объект автора
     * @throws EmptyCacheException, если в кэше нет автора
     */
    Author change(String name, String country);

    /**
     * Очистить кэш
     */
    void clearCache();

    /**
     * Получить объект автора из кэша
     * @return кэшированный автор
     * @throws EmptyCacheException если в кэше нет автора
     */
    Author getCache();
}
