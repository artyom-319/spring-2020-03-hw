package com.etn319.service.api;

import com.etn319.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    /**
     * Количество книг
     * @return количество книг
     */
    long count();

    /**
     * Загрузить в кэш книгу по id
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
     * Сохраняет кэшированную книгу. В случае успеха очищает кэш
     * @return сохранённый объект книги
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет книги
     */
    Book save();

    /**
     * Удаляет книгу по id
     * @param id id книги, которую нужно удалить
     * @throws com.etn319.service.ServiceLayerException, если удаление не удалось выполнить
     */
    void deleteById(String id);

    /**
     * Найти книги по закэшированному жанру
     * @return список книг в закешированном жанре
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет жанра
     */
    List<Book> getByCachedGenre();

    /**
     * Найти книги по id жанра
     * @param title id жанра
     * @return список книг в данном жанре
     */
    List<Book> getByGenreTitle(String title);

    /**
     * Найти книги по закэшированному автору
     * @return список книг закэшированного автора
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет автора
     */
    List<Book> getByCachedAuthor();

    /**
     * Найти книги по id автора
     * @param id id автора
     * @return список книг данного автора
     */
    List<Book> getByAuthorId(String id);

    /**
     * Создать книгу и разместить её в кэше
     * @param title заголовок
     * @return объект книги
     */
    Book create(String title);

    /**
     * Изменить книгу в кэше
     * @param title заголовок
     * @return объект книги
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет книги
     */
    Book change(String title);

    /**
     * Привязать книгу из кэша к автору из кэша
     * @return текущая книга
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет автора или книги
     */
    Book wireAuthor();

    /**
     * Привязать книгу из кэша к жанру из кэша
     * @return текущая книга
     * @throws com.etn319.service.EmptyCacheException, если в кэше нет жанра или книги
     */
    Book wireGenre();

    /**
     * Очистить кэш книги
     */
    void clearCache();

    /**
     * Получить объект книги из кэша
     * @return кэшированная книга
     * @throws com.etn319.service.EmptyCacheException если в кэше нет книги
     */
    Book getCache();
}
