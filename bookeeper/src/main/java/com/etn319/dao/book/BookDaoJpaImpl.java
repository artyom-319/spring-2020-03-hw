package com.etn319.dao.book;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.model.Author;
import com.etn319.model.Book;
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
public class BookDaoJpaImpl implements BookDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("select count(book) from Book book", long.class);
        return query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getById(long id) {
        return Optional.ofNullable(entityManager.find(Book.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAll() {
        TypedQuery<Book> query = entityManager.createQuery("select book from Book book", Book.class);
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
        // todo: onDelete ??
        try {
            entityManager.remove(book);
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        // todo: onDelete ??
        int deleted;
        try {
            Query query = entityManager.createQuery("delete from Book book where book.id = :id");
            query.setParameter("id", id);
            deleted = query.executeUpdate();
        } catch (RuntimeException e) {
            throw new DaoLayerException(e);
        }
        if (deleted == 0)
            throw new EntityNotFoundException();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByGenre(Genre genre) {
        TypedQuery<Book> query = entityManager.createQuery("select book from Book book " +
                "where book.genre = :genre", Book.class);
        query.setParameter("genre", genre);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByGenreId(long genreId) {
        TypedQuery<Book> query = entityManager.createQuery("select book from Book book " +
                "where book.genre.id = :genreId", Book.class);
        query.setParameter("genreId", genreId);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByAuthor(Author author) {
        TypedQuery<Book> query = entityManager.createQuery("select book from Book book " +
                "where book.author = :author", Book.class);
        query.setParameter("author", author);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByAuthorId(long authorId) {
        TypedQuery<Book> query = entityManager.createQuery("select book from Book book " +
                "where book.author.id = :authorId", Book.class);
        query.setParameter("authorId", authorId);
        return query.getResultList();
    }
}
