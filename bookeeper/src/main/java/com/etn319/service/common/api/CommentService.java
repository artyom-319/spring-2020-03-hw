package com.etn319.service.common.api;

import com.etn319.model.Book;
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
     * Поиск комментария по id
     * @param id идентификатор искомого комментария
     * @return объект комментария, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Comment> getById(String id);

    /**
     * Извлечь из базы первый попавшийся комментарий
     * @return объект комментария, упакованный в <code>Optional</code>, если в базе есть хоть один коммент
     * <code>Optional.empty()</code>, если база пустая
     */
    Optional<Comment> first();

    /**
     * Получить все комментарии
     * @return список комментариев
     */
    List<Comment> getAll();

    /**
     * Сохраняет комментарий
     * @param comment комментарий
     * @return обновлённый объект комментария
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     */
    Comment save(Comment comment);

    /**
     * Удаляет комментарий по id
     * @param id id комментария, который нужно удалить
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при удалении
     */
    void deleteById(String id);

    /**
     * Получить комментарии к книге
     * @return список комментариев
     */
    List<Comment> getByBook(Book book);

    /**
     * Получить комментарии от автора по его имени
     * @param name имя комментатора
     * @return список комментариев
     */
    List<Comment> getByCommenterName(String name);
}
