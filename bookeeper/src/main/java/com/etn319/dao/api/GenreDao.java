package com.etn319.dao.api;

import com.etn319.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    /**
     * Количество жанров в базе
     * @return количество жанров в базе
     */
    long count();

    /**
     * Получить жанр по id
     * @param id идентификатор искомого жанра
     * @return объект жанра, упакованный в <code>Optional</code>
     * <code>Optional.empty()</code>, если не найден
     */
    Optional<Genre> getById(long id);

    /**
     * Получить все жанры из базы
     * @return список жанров
     */
    List<Genre> getAll();

    /**
     * Сохраняет жанр в базе. Если задан ненулевой ID, произойдёт вставка в таблицу, в противном случае - обновление
     * @param genre объект жанра
     * @return сохранённый в базе объект жанра
     * @throws com.etn319.dao.DaoLayerException, если нарушено какое-либо ограничение таблицы
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    Genre save(Genre genre);

    /**
     * Удаляет жанр из базы. Если задан нулевой id, ничего не произойдёт
     * @param genre жанр, который нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     */
    void delete(Genre genre);

    /**
     * Удаляет жанр из базы по его id
     * @param id id жанра, который нужно удалить
     * @throws com.etn319.dao.DaoLayerException, если удаление не удалось выполнить
     * @throws com.etn319.dao.EntityNotFoundException, если по id не найдено записей
     */
    void deleteById(long id);
}
