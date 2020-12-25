package com.etn319.service.caching.api;

import com.etn319.model.Book;
import com.etn319.service.caching.EmptyCacheException;
import com.etn319.service.common.api.BookService;

import java.util.List;

public interface BookCachingService extends BookService {
    /**
     * Создать книгу и разместить её в кэше
     * @param title заголовок
     * @return объект книги
     */
    Book create(String title);

    /**
     * Сохраняет кэшированную книгу. В случае успеха очищает кэш
     * @return сохранённый объект книги
     * @throws com.etn319.service.ServiceLayerException, если произошла ошибка при сохранении
     * @throws EmptyCacheException, если в кэше нет книги
     */
    Book save();

    /**
     * Изменить книгу в кэше
     * @param title заголовок
     * @return объект книги
     * @throws EmptyCacheException, если в кэше нет книги
     */
    Book change(String title);

    /**
     * Привязать книгу из кэша к автору из кэша
     * @return текущая книга
     * @throws EmptyCacheException, если в кэше нет автора или книги
     */
    Book wireAuthor();

    /**
     * Привязать книгу из кэша к жанру из кэша
     * @return текущая книга
     * @throws EmptyCacheException, если в кэше нет жанра или книги
     */
    Book wireGenre();

    /**
     * Найти книги по закэшированному автору
     * @return список книг закэшированного автора
     * @throws EmptyCacheException, если в кэше нет автора
     */
    List<Book> getByCachedAuthor();

    /**
     * Найти книги по закэшированному жанру
     * @return список книг в закешированном жанре
     * @throws EmptyCacheException, если в кэше нет жанра
     */
    List<Book> getByCachedGenre();

    /**
     * Очистить кэш книги
     */
    void clearCache();

    /**
     * Получить объект книги из кэша
     * @return кэшированная книга
     * @throws EmptyCacheException если в кэше нет книги
     */
    Book getCache();
}
