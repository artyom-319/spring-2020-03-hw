package com.etn319.dao.jpa;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.GenreDao;
import com.etn319.model.Genre;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Profile("jpa")
public class GenreDaoJpaImpl implements GenreDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("select count(genre) from Genre genre", long.class);
        return query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Genre> getById(long id) {
        return Optional.ofNullable(entityManager.find(Genre.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAll() {
        TypedQuery<Genre> query = entityManager.createQuery("select genre from Genre genre", Genre.class);
        return query.getResultList();
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0L)
            entityManager.persist(genre);
        else
            entityManager.merge(genre);
        return genre;
    }

    @Override
    public void delete(Genre genre) {
        try {
            entityManager.remove(genre);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        int deleted;
        try {
            Query query = entityManager.createQuery("delete from Genre genre where genre.id = :id");
            query.setParameter("id", id);
            deleted = query.executeUpdate();
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
        if (deleted == 0)
            throw new EntityNotFoundException();
    }
}
