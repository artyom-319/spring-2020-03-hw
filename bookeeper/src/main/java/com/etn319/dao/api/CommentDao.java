package com.etn319.dao.api;

import com.etn319.model.Book;
import com.etn319.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    /**
     * Количество комментариев в базе
     * @return количество комментариев в базе
     */
    long count();

    /**
     * Получить комментарий по id
     * @param id идентификатор искомого комментария
     * @return объект комментария, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Comment> getById(long id);

    /**
     * Получить все комментарии из базы
     * @return список комментариев
     */
    List<Comment> getAll();

    /**
     * Сохраняет комментарий в базе. Если задан ненулевой ID, произойдёт вставка в таблицу, в противном случае - обновление
     * @param comment объект комментария
     * @return сохранённый в базе объект комментария
     * @throws com.etn319.dao.DaoLayerException, если нарушено какое-либо ограничение таблицы
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    Comment save(Comment comment);

    /**
     * Удаляет комментарий из базы. Если задан нулевой id, ничего не произойдёт
     * @param comment комментарий, которую нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     */
    void delete(Comment comment);

    /**
     * Удаляет комментарий из базы по его id
     * @param id id комментария, который нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    void deleteById(long id);

    /**
     * Получить комментарии к книге
     * @param book книга
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
