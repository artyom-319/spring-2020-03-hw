package com.etn319.dao.jpa;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.BookDao;
import com.etn319.model.Book;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class BookDaoJpaImpl implements BookDao {
    private static final String FETCH_GRAPH_HINT = "javax.persistence.fetchgraph";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("select count(book) from Book book", long.class);
        return query.getSingleResult();
    }

    @Override
    public Optional<Book> getById(long id) {
        return Optional.ofNullable(entityManager.find(Book.class, id));
    }

    @Override
    public List<Book> getAll() {
        EntityGraph entityGraph = entityManager.getEntityGraph(Book.FETCH_GRAPH_NAME);
        TypedQuery<Book> query = entityManager.createQuery("select b from Book b ", Book.class);
        query.setHint(FETCH_GRAPH_HINT, entityGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0L)
            entityManager.persist(book);
        else
            entityManager.merge(book);
        return book;
    }

    @Override
    public void delete(Book book) {
        try {
            entityManager.remove(book);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        var book = getById(id).orElseThrow(EntityNotFoundException::new);
        try {
            entityManager.remove(book);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }
}
