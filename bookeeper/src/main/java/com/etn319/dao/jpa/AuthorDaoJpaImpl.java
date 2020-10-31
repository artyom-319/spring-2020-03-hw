package com.etn319.dao.jpa;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.AuthorDao;
import com.etn319.model.Author;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDaoJpaImpl implements AuthorDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("select count(a) from Author a", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Optional<Author> getById(long id) {
        return Optional.ofNullable(entityManager.find(Author.class, id));
    }

    @Override
    public List<Author> getAll() {
        TypedQuery<Author> query = entityManager.createQuery("select a from Author a", Author.class);
        return query.getResultList();
    }

    @Override
    public Author save(Author author) {
        long authorId = author.getId();
        if (author.getId() == 0L) {
            entityManager.persist(author);
            entityManager.flush();
        } else {
            checkExists(authorId);
            author = entityManager.merge(author);
        }
        return author;
    }

    @Override
    public void delete(Author author) {
        try {
            entityManager.remove(author);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        var author = getById(id).orElseThrow(EntityNotFoundException::new);
        try {
            entityManager.remove(author);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    private boolean exists(long id) {
        Author author = entityManager.find(Author.class, id);
        return (author != null);
    }

    private void checkExists(long id) {
        if (!exists(id))
            throw new EntityNotFoundException();
    }
}
