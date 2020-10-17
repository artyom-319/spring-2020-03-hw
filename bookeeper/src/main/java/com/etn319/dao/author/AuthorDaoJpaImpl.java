package com.etn319.dao.author;

import com.etn319.dao.DaoLayerException;
import com.etn319.model.Author;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Profile("jpa")
public class AuthorDaoJpaImpl implements AuthorDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("select count(a) from Author a", Long.class);
        return query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> getById(long id) {
        return Optional.ofNullable(entityManager.find(Author.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> getAll() {
        TypedQuery<Author> query = entityManager.createQuery("select a from Author a", Author.class);
        return query.getResultList();
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == 0L)
            entityManager.persist(author);
        else
            entityManager.merge(author);
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
        int deleted;
        try {
            Query query = entityManager.createQuery("delete from Author a where a.id = :id");
            query.setParameter("id", id);
            deleted = query.executeUpdate();
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
        if (deleted == 0)
            throw new EntityNotFoundException();
    }
}
