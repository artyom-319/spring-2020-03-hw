package com.etn319.dao.genre;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.mappers.GenreRowMapper;
import com.etn319.model.Genre;
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
public class GenreDaoImpl implements GenreDao {
    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public long count() {
        return jdbcTemplate.getJdbcOperations().queryForObject("select count(1) from genres", long.class);
    }

    @Override
    public Optional<Genre> getById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from genres where id = :id",
                    Collections.singletonMap("id", id), new GenreRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.getJdbcOperations().query("select * from genres", new GenreRowMapper());
    }

    private Genre insert(final Genre genre) {
        try {
            var params = new MapSqlParameterSource("title", genre.getTitle());
            var keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update("insert into genres (title) values (:title)", params, keyHolder, new String[]{"id"});
            genre.setId(keyHolder.getKey().longValue());
            return genre;
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }

    private Genre update(Genre genre) {
        try {
            var params = new MapSqlParameterSource()
                    .addValue("id", genre.getId())
                    .addValue("title", genre.getTitle());
            int updated = jdbcTemplate.update("update genres set title = :title where id = :id", params);
            if (updated == 0)
                throw new EntityNotFoundException();
            return genre;
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0L)
            return insert(genre);
        else
            return update(genre);
    }

    @Override
    public void delete(Genre genre) {
        if (genre.getId() != 0L)
            deleteById(genre.getId());
    }

    @Override
    public void deleteById(long id) {
        try {
            int affected = jdbcTemplate.update("delete from genres where id = :id", Collections.singletonMap("id", id));
            if (affected == 0)
                throw new EntityNotFoundException();
        } catch (DataAccessException e) {
            throw new DaoLayerException(e);
        }
    }
}
