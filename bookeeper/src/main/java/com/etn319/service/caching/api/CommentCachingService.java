package com.etn319.service.caching.api;

import com.etn319.model.Comment;
import com.etn319.service.caching.EmptyCacheException;
import com.etn319.service.common.api.CommentService;

import java.util.List;

public interface CommentCachingService extends CommentService {
    /**
     * Создать комментарий к кэшированной книге и разместить его в кэше
     * @param text текст комментария
     * @param commenter имя комментатора
     * @return объект комментария
     * @throws EmptyCacheException, если в кэше нет книги
     */
    Comment create(String text, String commenter);

    /**
     * Сохраняет кэшированный комментарий. В случае успеха очищает кэш
     * @return сохранённый объект комментария
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     * @throws EmptyCacheException, если в кэше нет комментария
     */
    Comment save();

    /**
     * Изменить комментарий в кэше
     * @param text екст комментария
     * @param commenter имя комментатора
     * @return объект комментария
     * @throws EmptyCacheException, если в кэше нет комментария
     */
    Comment change(String text, String commenter);

    /**
     * Привязать книгу к комментарию
     * @return текущий комментарий
     * @throws EmptyCacheException, если в кэше нет книги или комментария
     */
    Comment wireBook();

    /**
     * Получить комментарии к кэшированной книге
     * @return список комментариев
     */
    List<Comment> getByBook();

    /**
     * Очистить кэш
     */
    void clearCache();

    /**
     * Получить объект комментария из кэша
     * @return кэшированный комментарий
     * @throws EmptyCacheException если в кэше нет комментария
     */
    Comment getCache();
}
