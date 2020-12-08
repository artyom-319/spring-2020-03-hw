package com.etn319.service.api;

import com.etn319.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    /**
     * Количество авторов
     * @return количество авторов
     */
    long count();

    /**
     * Загрузить в кэш автора по id
     * @param id идентификатор искомого автора
     * @return объект автора, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Author> getById(String id);

    /**
     * Получить всех авторов
     * @return список авторов
     */
    List<Author> getAll();

    /**
     * Сохраняет кэшированного автора. В случае успеха очищает кэш
     * @return сохранённый объект автора
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет автора
     */
    Author save();

    /**
     * Удаляет автора по id
     * @param id id автора, которого нужно удалить
     * @throws com.etn319.service.ServiceLayerException, если удаление не удалось выполнить
     */
    void deleteById(String id);

    /**
     * Создать автора и разместить его в кэше
     * @param name имя автора
     * @param country страна проживания автора
     * @return объект автора
     */
    Author create(String name, String country);

    /**
     * Изменить автора в кэше. Если какой-либо из параметров {@code null}, то соответствующее поле
     * автора не меняется
     * @param name имя автора
     * @param country страна проживания автора
     * @return объект автора
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет автора
     */
    Author change(String name, String country);

    /**
     * Очистить кэш
     */
    void clearCache();

    /**
     * Получить объект автора из кэша
     * @return кэшированный автор
     * @throws com.etn319.service.EmptyCacheException если в кэше нет автора
     */
    Author getCache();
}
