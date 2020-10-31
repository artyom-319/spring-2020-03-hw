package com.etn319.dao.datajpa;

import com.etn319.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Получить комментарии от автора по его имени
     * @param name имя комментатора
     * @return список комментариев
     */
    List<Comment> findByCommenter(String name);
}
