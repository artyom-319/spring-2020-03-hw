package com.etn319.dao.mappers;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class BookRowMapper implements RowMapper<Book> {
    private String aliasForAuthor;
    private String aliasForGenre;
    private Map<String, String> columnLabels = Map.of("id", "id", "title", "title");

    public BookRowMapper(String aliasForBook, String aliasForAuthor, String aliasForGenre) {
        this.aliasForAuthor = aliasForAuthor;
        this.aliasForGenre = aliasForGenre;
        addPrefixToColumnLabels(aliasForBook + ".");
    }

    @Override
    public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong(columnLabels.get("id"));
        String title = resultSet.getString(columnLabels.get("title"));
        Author author = new AuthorRowMapper(aliasForAuthor).mapRow(resultSet, rowNum);
        Genre genre = new GenreRowMapper(aliasForGenre).mapRow(resultSet, rowNum);
        return new Book(id, title, author, genre);
    }

    private void addPrefixToColumnLabels(String prefix) {
        for (var entry : columnLabels.entrySet()) {
            String value = entry.getValue();
            entry.setValue(prefix + value);
        }
    }
}
