package com.etn319.dao.jpa;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.CommentDao;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentDaoJpaImpl implements CommentDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public long count() {
        TypedQuery<Long> query = em.createQuery("select count(c) from Comment c", long.class);
        return query.getSingleResult();
    }

    @Override
    public Optional<Comment> getById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public List<Comment> getAll() {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c join fetch c.book b join fetch b.author join fetch b.genre", Comment.class);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0L) {
            em.persist(comment);
            em.flush();
        } else
            em.merge(comment);
        return comment;
    }

    @Override
    public void delete(Comment comment) {
        try {
            em.remove(comment);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        int deleted;
        try {
            Query query = em.createQuery("delete from Comment c where c.id = :id");
            query.setParameter("id", id);
            deleted = query.executeUpdate();
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
        if (deleted == 0)
            throw new EntityNotFoundException();
    }

    @Override
    public List<Comment> getByBook(Book book) {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c join fetch c.book b join fetch b.author join fetch b.genre where c.book = :book", Comment.class);
        query.setParameter("book", book);
        return query.getResultList();
    }

    @Override
    public List<Comment> getByCommenterName(String name) {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c join fetch c.book b join fetch b.author join fetch b.genre where c.commenter = :commenter", Comment.class);
        query.setParameter("commenter", name);
        return query.getResultList();
    }
}
