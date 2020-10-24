package com.etn319.dao.jpa;

import com.etn319.dao.api.CommentDao;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CommentDaoJpaImpl implements CommentDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Comment> getCommentsByBook(Book book) {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c join fetch c.book b join fetch b.author join fetch b.genre where c.book = :book", Comment.class);
        query.setParameter("book", book);
        return query.getResultList();
    }

    @Override
    public List<Comment> getCommentsByCommenterName(String name) {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c join fetch c.book b join fetch b.author join fetch b.genre where c.commenter = :commenter", Comment.class);
        query.setParameter("commenter", name);
        return query.getResultList();
    }
}
