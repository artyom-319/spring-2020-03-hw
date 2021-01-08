package com.etn319.service.common.api;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;

import java.util.List;
import java.util.Optional;

public interface BookService {
    /**
     * Количество книг
     * @return количество книг
     */
    long count();

    /**
     * Поиск книги по id
     * @param id идентификатор искомой книги
     * @return объект книги, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найдена
     */
    Optional<Book> getById(String id);

    /**
     * Извлечь из базы случайную книгу
     * @return объект книги, упакованный в <code>Optional</code>, если в базе есть хоть одна книга
     * <code>Optional.empty()</code>, если база пустая
     */
    Optional<Book> first();

    /**
     * Получить все книги
     * @return список книг
     */
    List<Book> getAll();

    /**
     * Сохраняет книгу
     * @param book книга, которую нужно сохранить
     * @return обновлённый объект книги
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     */
    Book save(Book book);

    /**
     * Удаляет книгу по id
     * @param id id книги, которую нужно удалить
     * @throws com.etn319.service.ServiceLayerException, если удаление не удалось выполнить
     */
    void deleteById(String id);

    /**
     * Найти книги по жанру
     * @param genre жанр
     * @return список книг в указанном жанре
     */
    List<Book> getByGenre(Genre genre);

    /**
     * Найти книги по id жанра
     * @param title id жанра
     * @return список книг в данном жанре
     */
    List<Book> getByGenreTitle(String title);

    /**
     * Найти книги по автору
     * @param author автор
     * @return список книг автора
     */
    List<Book> getByAuthor(Author author);

    /**
     * Найти книги по id автора
     * @param id id автора
     * @return список книг данного автора
     */
    List<Book> getByAuthorId(String id);
}
