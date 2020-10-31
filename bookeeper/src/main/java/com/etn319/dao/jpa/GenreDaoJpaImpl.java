package com.etn319.dao.jpa;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.GenreDao;
import com.etn319.model.Genre;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDaoJpaImpl implements GenreDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("select count(genre) from Genre genre", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Optional<Genre> getById(long id) {
        return Optional.ofNullable(entityManager.find(Genre.class, id));
    }

    @Override
    public List<Genre> getAll() {
        TypedQuery<Genre> query = entityManager.createQuery("select genre from Genre genre", Genre.class);
        return query.getResultList();
    }

    @Override
    public Genre save(Genre genre) {
        long genreId = genre.getId();
        if (genreId == 0L) {
            entityManager.persist(genre);
            entityManager.flush();
        } else {
            checkExists(genreId);
            genre = entityManager.merge(genre);
        }
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
        var genre = getById(id).orElseThrow(EntityNotFoundException::new);
        try {
            entityManager.remove(genre);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    private boolean exists(long id) {
        Genre genre = entityManager.find(Genre.class, id);
        return (genre != null);
    }

    private void checkExists(long id) {
        if (!exists(id))
            throw new EntityNotFoundException();
    }
}
