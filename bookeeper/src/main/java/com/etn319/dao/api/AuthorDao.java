package com.etn319.dao.api;

import com.etn319.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    /**
     * Количество авторов в базе
     * @return количество авторов в базе
     */
    long count();

    /**
     * Получить автора по id
     * @param id идентификатор искомого автора
     * @return объект автора, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Author> getById(long id);

    /**
     * Получить всех авторов из базы
     * @return список авторов
     */
    List<Author> getAll();

    /**
     * Сохраняет автора в базе. Если задан ненулевой ID, произойдёт вставка в таблицу, в противном случае - обновление
     * @param author объект автора
     * @return сохранённый в базе объект автора
     * @throws com.etn319.dao.DaoLayerException, если нарушено какое-либо ограничение таблицы
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    Author save(Author author);

    /**
     * Удаляет автора из базы. Если задан нулевой id, ничего не произойдёт
     * @param author автор, которого нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     */
    void delete(Author author);

    /**
     * Удаляет автора из базы по его id
     * @param id id автора, которого нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    void deleteById(long id);
}
