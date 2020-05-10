package com.etn319.dao.book;

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

@SuppressWarnings("ConstantConditions")
@Repository
@RequiredArgsConstructor
public class BookDaoImpl implements BookDao {
    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public int count() {
        return jdbcTemplate.getJdbcOperations().queryForObject("select count(1) from books", int.class);
    }

    @Override
    public Book getById(long id) {
        try {
            return jdbcTemplate.queryForObject("select * from books " +
                            "left join authors on books.author_id = authors.id " +
                            "left join genres on books.genre_id = genres.id where books.id = :id",
                    Collections.singletonMap("id", id),
                    new BookRowMapper("books", "authors", "genres"));
        } catch (EmptyResultDataAccessException ignored) {
            // todo: вынести в аспект
            return null;
        }
    }

    @Override
    public List<Book> getAll() {
        return jdbcTemplate.getJdbcOperations().query("select * from books " +
                "join authors on books.author_id = authors.id " +
                "join genres on books.genre_id = genres.id",
                new BookRowMapper("books", "authors", "genres"));
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
    public boolean update(Book book) {
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
        return updated > 0;
    }

    @Override
    public boolean delete(Book book) {
        return deleteById(book.getId());
    }

    @Override
    public boolean deleteById(long id) {
        int affected = jdbcTemplate.update("delete from books where id = :id", Collections.singletonMap("id", id));
        return affected > 0;
    }
}
