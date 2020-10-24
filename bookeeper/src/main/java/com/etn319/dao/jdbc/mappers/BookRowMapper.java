package com.etn319.dao.jdbc.mappers;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BookRowMapper implements RowMapper<Book> {
    private AuthorRowMapper authorRowMapper;
    private GenreRowMapper genreRowMapper;
    private Map<String, String> columnLabels = Map.of("id", "id", "title", "title");

    public BookRowMapper(String bookPrefix, String authorPrefix, String genrePrefix, String delimiter) {
        addPrefixToColumnLabels(bookPrefix + delimiter);
        authorRowMapper = new AuthorRowMapper(authorPrefix, delimiter);
        genreRowMapper = new GenreRowMapper(genrePrefix, delimiter);
    }

    @Override
    public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong(columnLabels.get("id"));
        String title = resultSet.getString(columnLabels.get("title"));
        Author author = authorRowMapper.mapRow(resultSet, rowNum);
        Genre genre = genreRowMapper.mapRow(resultSet, rowNum);
        return new Book(id, title, author, genre);
    }

    private void addPrefixToColumnLabels(String prefix) {
        Map<String, String> columnLabelsWithPrefix = new HashMap<>();
        columnLabels.forEach((k, v) -> columnLabelsWithPrefix.put(k, prefix + v));
        columnLabels = columnLabelsWithPrefix;
    }
}
