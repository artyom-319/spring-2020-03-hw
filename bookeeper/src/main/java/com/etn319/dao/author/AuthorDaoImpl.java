package com.etn319.dao.author;

import com.etn319.dao.mappers.AuthorRowMapper;
import com.etn319.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("ConstantConditions")
@Repository
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {
    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public int count() {
        return jdbcTemplate.getJdbcOperations().queryForObject("select count(1) from authors", int.class);
    }

    @Override
    public Author getById(long id) {
        return jdbcTemplate.queryForObject("select * from authors where id = :id",
                Collections.singletonMap("id", id), new AuthorRowMapper());
    }

    @Override
    public List<Author> getAll() {
        return jdbcTemplate.getJdbcOperations().query("select * from authors", new AuthorRowMapper());
    }

    @Override
    public Author insert(final Author author) {
        var params = new MapSqlParameterSource()
                .addValue("name", author.getName())
                .addValue("country", author.getCountry());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                "insert into authors (name, country) values (:name, :country)", params, keyHolder, new String[]{"id"});
        author.setId(keyHolder.getKey().longValue());
        return author;
    }

    @Override
    public boolean update(Author author) {
        var params = new MapSqlParameterSource()
                .addValue("id", author.getId())
                .addValue("name", author.getName())
                .addValue("country", author.getCountry());
        int updated =
                jdbcTemplate.update("update authors set name = :name, country = :country where id = :id", params);
        return updated > 0;
    }

    @Override
    public boolean delete(Author author) {
        return deleteById(author.getId());
    }

    @Override
    public boolean deleteById(long id) {
        int affected = jdbcTemplate.update("delete from authors where id = :id", Collections.singletonMap("id", id));
        return affected > 0;
    }
}
