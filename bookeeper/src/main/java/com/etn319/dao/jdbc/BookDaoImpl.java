package com.etn319.dao.jdbc;

import com.etn319.dao.ConnectedEntityDoesNotExistException;
import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.BookDao;
import com.etn319.dao.jdbc.mappers.BookRowMapper;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
@Repository
@RequiredArgsConstructor
@Profile("jdbc")
public class BookDaoImpl implements BookDao {
    private static final String DOT = ".";
    private static final String UNDERLINE = "_";
    private final NamedParameterJdbcOperations jdbcTemplate;
    private List<String> selectables = List.of(
            "books.id", "books.title",
            "authors.id", "authors.name", "authors.country",
            "genres.id", "genres.title"
    );
    private String aliasSelectables = toAliasString(selectables);

    @Override
    public long count() {
        return jdbcTemplate.getJdbcOperations().queryForObject("select count(1) from books", long.class);
    }

    @Override
    public Optional<Book> getById(long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            String.format("select %s from books " +
                                            "left join authors on books.author_id = authors.id " +
                                            "left join genres on books.genre_id = genres.id where books.id = :id",
                                    aliasSelectables),
                            Collections.singletonMap("id", id),
                            new BookRowMapper("books", "authors", "genres", UNDERLINE)
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> getAll() {
        return jdbcTemplate.getJdbcOperations().query(("select * from books " +
                "left join authors on books.author_id = authors.id " +
                "left join genres on books.genre_id = genres.id")
                        .replace("*", aliasSelectables),
                new BookRowMapper("books", "authors", "genres", UNDERLINE));
    }

    private Book insert(final Book book) {
        try {
            checkForEmptyEntities(book);
            var params = new MapSqlParameterSource()
                    .addValue("title", book.getTitle())
                    .addValue("author", book.getAuthor().getId())
                    .addValue("genre", book.getGenre().getId());
            var keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update("insert into books (title, author_id, genre_id) values (:title, :author, :genre)",
                    params, keyHolder, new String[]{"id"});
            book.setId(keyHolder.getKey().longValue());
            return book;
        } catch (DataIntegrityViolationException e) {
            throw new ConnectedEntityDoesNotExistException(e);
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }

    private Book update(Book book) {
        try {
            checkForEmptyEntities(book);
            var params = new MapSqlParameterSource()
                    .addValue("id", book.getId())
                    .addValue("title", book.getTitle())
                    .addValue("author", book.getAuthor().getId())
                    .addValue("genre", book.getGenre().getId());
            int updated =
                    jdbcTemplate.update("update books set title = :title, author_id = :author, genre_id = :genre" +
                            " where id = :id", params);
            if (updated == 0)
                throw new EntityNotFoundException();
            return book;
        } catch (DataIntegrityViolationException e) {
            throw new ConnectedEntityDoesNotExistException(e);
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0L)
            return insert(book);
        else
            return update(book);
    }

    private void checkForEmptyEntities(Book book) {
        Objects.requireNonNull(book);
        long authorId = Optional.ofNullable(book.getAuthor()).map(Author::getId).orElse(0L);
        long genreId = Optional.ofNullable(book.getGenre()).map(Genre::getId).orElse(0L);
        if (authorId == 0L || genreId == 0L)
            throw new ConnectedEntityDoesNotExistException();
    }

    @Override
    public void delete(Book book) {
        deleteById(book.getId());
    }

    @Override
    public void deleteById(long id) {
        try {
            int affected = jdbcTemplate.update(
                    "delete from books where id = :id", Collections.singletonMap("id", id));
            if (affected == 0)
                throw new EntityNotFoundException();
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public List<Book> getByGenre(Genre genre) {
        return getByGenreId(genre.getId());
    }

    @Override
    public List<Book> getByGenreId(long genreId) {
        return jdbcTemplate.query(("select * from books " +
                        "left join authors on books.author_id = authors.id " +
                        "join genres on books.genre_id = genres.id where books.genre_id = :genreId")
                        .replace("*", aliasSelectables),
                Collections.singletonMap("genreId", genreId),
                new BookRowMapper("books", "authors", "genres", UNDERLINE));
    }

    @Override
    public List<Book> getByAuthor(Author author) {
        return getByAuthorId(author.getId());
    }

    @Override
    public List<Book> getByAuthorId(long authorId) {
        return jdbcTemplate.query(("select * from books " +
                        "join authors on books.author_id = authors.id " +
                        "left join genres on books.genre_id = genres.id where books.author_id = :authorId")
                        .replace("*", aliasSelectables),
                Collections.singletonMap("authorId", authorId),
                new BookRowMapper("books", "authors", "genres", UNDERLINE));
    }

    private String toAliasString(List<String> selectables) {
        return selectables.stream()
                .map(s -> s + " as " + s.replace(DOT, UNDERLINE))
                .collect(Collectors.joining(", "));
    }
}