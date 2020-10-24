package com.etn319.dao.api;

import com.etn319.model.Book;
import com.etn319.model.Comment;

import java.util.List;

public interface CommentDao {
    /**
     * Получить комментарии к книге
     * @param book книга
     * @return список комментариев
     */
    List<Comment> getCommentsByBook(Book book);

    /**
     * Получить комментарии от автора по его имени
     * @param name имя комментатора
     * @return список комментариев
     */
    List<Comment> getCommentsByCommenterName(String name);
}
