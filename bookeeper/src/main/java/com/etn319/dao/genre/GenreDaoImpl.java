package com.etn319.dao.genre;

import com.etn319.dao.NoEntityFoundException;
import com.etn319.dao.mappers.GenreRowMapper;
import com.etn319.model.Genre;
import lombok.RequiredArgsConstructor;
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
public class GenreDaoImpl implements GenreDao {
    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public int count() {
        return jdbcTemplate.getJdbcOperations().queryForObject("select count(1) from genres", int.class);
    }

    @Override
    public Genre getById(long id) {
        try {
            return jdbcTemplate.queryForObject("select * from genres where id = :id",
                    Collections.singletonMap("id", id), new GenreRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NoEntityFoundException();
        }
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.getJdbcOperations().query("select * from genres", new GenreRowMapper());
    }

    @Override
    public Genre insert(final Genre genre) {
        var params = new MapSqlParameterSource("title", genre.getTitle());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update("insert into genres (title) values (:title)", params, keyHolder, new String[]{"id"});
        genre.setId(keyHolder.getKey().longValue());
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        var params = new MapSqlParameterSource()
                .addValue("id", genre.getId())
                .addValue("title", genre.getTitle());
        int updated = jdbcTemplate.update("update genres set title = :title where id = :id", params);
        if (updated == 0)
            throw new NoEntityFoundException();
        return genre;
    }

    @Override
    public void delete(Genre genre) {
        deleteById(genre.getId());
    }

    @Override
    public void deleteById(long id) {
        int affected = jdbcTemplate.update("delete from genres where id = :id", Collections.singletonMap("id", id));
        if (affected == 0)
            throw new NoEntityFoundException();
    }
}
