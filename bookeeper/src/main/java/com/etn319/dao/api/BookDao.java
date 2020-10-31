package com.etn319.dao.api;

import com.etn319.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    /**
     * Количество книг в базе
     * @return количество книг в базе
     */
    long count();

    /**
     * Получить книгу по id
     * @param id идентификатор искомой книги
     * @return объект книги, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Book> findById(long id);

    /**
     * Получить все книги из базы
     * @return список книг
     */
    List<Book> findAll();

    /**
     * Сохраняет книгу в базе. Если задан ненулевой ID, произойдёт вставка в таблицу, в противном случае - обновление
     * @param book объект книги
     * @return сохранённый в базе объект книги
     * @throws com.etn319.dao.DaoLayerException, если нарушено какое-либо ограничение таблицы
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    Book save(Book book);

    /**
     * Удаляет книгу из базы. Если задан нулевой id, ничего не произойдёт
     * @param book книга, которую нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     */
    void delete(Book book);

    /**
     * Удаляет книгу из базы по её id
     * @param id id книги, которого нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    void deleteById(long id);
}
