package com.etn319.service.api;

import com.etn319.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    /**
     * Количество комментариев
     * @return количество комментариев
     */
    long count();

    /**
     * Загрузить в кэш комментарий по id
     * @param id идентификатор искомого комментария
     * @return объект комментария, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Comment> getById(String id);

    /**
     * Получить все комментарии
     * @return список комментариев
     */
    List<Comment> getAll();

    /**
     * Сохраняет кэшированный комментарий. В случае успеха очищает кэш
     * @return сохранённый объект комментария
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет комментария
     */
    Comment save();

    /**
     * Удаляет комментарий по id
     * @param id id комментария, который нужно удалить
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при удалении
     */
    void deleteById(String id);

    /**
     * Получить комментарии к кэшированной книге
     * @return список комментариев
     */
    List<Comment> getByBook();

    /**
     * Получить комментарии от автора по его имени
     * @param name имя комментатора
     * @return список комментариев
     */
    List<Comment> getByCommenterName(String name);

    /**
     * Создать комментарий к кэшированной книге и разместить его в кэше
     * @param text текст комментария
     * @param commenter имя комментатора
     * @return объект комментария
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет книги
     */
    Comment create(String text, String commenter);

    /**
     * Изменить комментарий в кэше
     * @param text екст комментария
     * @param commenter имя комментатора
     * @return объект комментария
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет комментария
     */
    Comment change(String text, String commenter);

    /**
     * Привязать книгу к комментарию
     * @return текущий комментарий
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет книги или комментария
     */
    Comment wireBook();

    /**
     * Очистить кэш
     */
    void clearCache();

    /**
     * Получить объект комментария из кэша
     * @return кэшированный комментарий
     * @throws com.etn319.service.EmptyCacheException если в кэше нет комментария
     */
    Comment getCache();
}
