package com.etn319.dao.book;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.mappers.BookRowMapper;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
@Repository
@RequiredArgsConstructor
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
    public int count() {
        return jdbcTemplate.getJdbcOperations().queryForObject("select count(1) from books", int.class);
    }

    @Override
    public Book getById(long id) {
        try {
            return jdbcTemplate.queryForObject(("select * from books " +
                            "left join authors on books.author_id = authors.id " +
                            "left join genres on books.genre_id = genres.id where books.id = :id")
                                    .replace("*", aliasSelectables),
                    Collections.singletonMap("id", id),
                    new BookRowMapper("books", "authors", "genres", UNDERLINE));
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(e);
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

    @Override
    public Book insert(final Book book) {
        Objects.requireNonNull(book);
        Author author = Objects.requireNonNull(book.getAuthor(), "author");
        Genre genre = Objects.requireNonNull(book.getGenre(), "genre");

        var params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author", author.getId())
                .addValue("genre", genre.getId());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update("insert into books (title, author_id, genre_id) values (:title, :author, :genre)",
                params, keyHolder, new String[]{"id"});
        book.setId(keyHolder.getKey().longValue());
        return book;
    }

    @Override
    public Book update(Book book) {
        // todo: author - NPE ?
        // todo: genre - NPE ?
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
    }

    @Override
    public void delete(Book book) {
        deleteById(book.getId());
    }

    @Override
    public void deleteById(long id) {
        int affected = jdbcTemplate.update("delete from books where id = :id", Collections.singletonMap("id", id));
        if (affected == 0)
            throw new EntityNotFoundException();
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
