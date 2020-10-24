package com.etn319.dao.jdbc;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.AuthorDao;
import com.etn319.dao.jdbc.mappers.AuthorRowMapper;
import com.etn319.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
@Repository
@Profile("jdbc")
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {
    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public long count() {
        return jdbcTemplate.getJdbcOperations().queryForObject("select count(1) from authors", long.class);
    }

    @Override
    public Optional<Author> getById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from authors where id = :id",
                    Collections.singletonMap("id", id), new AuthorRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Author> getAll() {
        return jdbcTemplate.getJdbcOperations().query("select * from authors", new AuthorRowMapper());
    }

    private Author insert(final Author author) {
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

    private Author update(Author author) {
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
    public Author save(Author author) {
        if (author.getId() == 0L)
            return insert(author);
        else
            return update(author);
    }

    @Override
    public void delete(Author author) {
        if (author.getId() != 0L)
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
