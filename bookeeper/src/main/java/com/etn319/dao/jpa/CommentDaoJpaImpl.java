package com.etn319.dao.jpa;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.CommentDao;
import com.etn319.model.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentDaoJpaImpl implements CommentDao {
    private static final String FETCH_GRAPH_HINT = "javax.persistence.fetchgraph";

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
        EntityGraph entityGraph = em.getEntityGraph(Comment.FETCH_GRAPH_NAME);
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c", Comment.class);
        query.setHint(FETCH_GRAPH_HINT, entityGraph);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        long commentId = comment.getId();
        if (commentId == 0L) {
            em.persist(comment);
            em.flush();
        } else {
            checkExists(commentId);
            comment = em.merge(comment);
        }
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
        var comment = getById(id).orElseThrow(EntityNotFoundException::new);
        try {
            em.remove(comment);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public List<Comment> getByCommenterName(String name) {
        EntityGraph entityGraph = em.getEntityGraph(Comment.FETCH_GRAPH_NAME);
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c where c.commenter = :commenter", Comment.class);
        query.setParameter("commenter", name);
        query.setHint(FETCH_GRAPH_HINT, entityGraph);
        return query.getResultList();
    }

    private boolean exists(long id) {
        Comment comment = em.find(Comment.class, id);
        return (comment != null);
    }

    private void checkExists(long id) {
        if (!exists(id))
            throw new EntityNotFoundException();
    }
}
