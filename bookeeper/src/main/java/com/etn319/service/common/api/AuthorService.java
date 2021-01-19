package com.etn319.service.common.api;

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
     * Проверить, существует ли объект с таким id
     * @param id id объекта
     * @return {@code true}, если существует, {@code false}, если нет
     */
    boolean exists(String id);

    /**
     * Поиск автора по id
     * @param id идентификатор искомого автора
     * @return объект автора, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Author> getById(String id);

    /**
     * Поиск автора по имени
     * @param name имя автора
     * @return объект автора, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Author> getByName(String name);

    /**
     * Извлечь из базы случайного автора
     * @return объект автора, упакованный в <code>Optional</code>, если в базе есть хоть один автор
     * <code>Optional.empty()</code>, если база пустая
     */
    Optional<Author> first();

    /**
     * Получить всех авторов
     * @return список авторов
     */
    List<Author> getAll();

    /**
     * Сохраняет автора
     * @param author автор, которого требуется сохранить
     * @return обновлённый объект автора
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     */
    Author save(Author author);

    /**
     * Удаляет автора по id
     * @param id id автора, которого нужно удалить
     * @throws com.etn319.service.ServiceLayerException, если удаление не удалось выполнить
     */
    void deleteById(String id);
}
