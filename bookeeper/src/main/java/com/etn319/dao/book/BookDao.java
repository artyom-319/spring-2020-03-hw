package com.etn319.dao.book;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;

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
    Optional<Book> getById(long id);

    /**
     * Получить все книги из базы
     * @return список книг
     */
    List<Book> getAll();

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

    /**
     * Поиск книг по жанру
     * @param genre жанр, в котором ищем книги
     * @return список книг в данном жанре
     */
    List<Book> getByGenre(Genre genre);

    /**
     * Поиск книг по id жанра
     * @param genreId id жанра, в котором ищем книги
     * @return список книг в данном жанре
     */
    List<Book> getByGenreId(long genreId);

    /**
     * Поиск книг по автору
     * @param author автор, у которого ищем книги
     * @return список книг данного автора
     */
    List<Book> getByAuthor(Author author);

    /**
     * Поиск книг по id автора
     * @param authorId id автора, у которого ищем книги
     * @return список книг данного автора
     */
    List<Book> getByAuthorId(long authorId);
}
