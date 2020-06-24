package com.etn319.dao.author;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.mappers.AuthorRowMapper;
import com.etn319.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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
        try {
            return jdbcTemplate.queryForObject("select * from authors where id = :id",
                    Collections.singletonMap("id", id), new AuthorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Author> getAll() {
        return jdbcTemplate.getJdbcOperations().query("select * from authors", new AuthorRowMapper());
    }

    @Override
    public Author insert(final Author author) {
        try {
            var params = new MapSqlParameterSource()
                    .addValue("name", author.getName())
                    .addValue("country", author.getCountry());
            var keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    "insert into authors (name, country) values (:name, :country)", params, keyHolder, new String[]{"id"});
            author.setId(keyHolder.getKey().longValue());
            return author;
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public Author update(Author author) {
        try {
            var params = new MapSqlParameterSource()
                    .addValue("id", author.getId())
                    .addValue("name", author.getName())
                    .addValue("country", author.getCountry());
            int updated =
                    jdbcTemplate.update("update authors set name = :name, country = :country where id = :id", params);
            if (updated == 0)
                throw new EntityNotFoundException();
            return author;
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public void delete(Author author) {
        deleteById(author.getId());
    }

    @Override
    public void deleteById(long id) {
        try {
            int affected = jdbcTemplate.update("delete from authors where id = :id", Collections.singletonMap("id", id));
            if (affected == 0)
                throw new EntityNotFoundException();
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }
}
