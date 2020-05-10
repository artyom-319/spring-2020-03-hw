package com.etn319.dao.mappers;

import com.etn319.model.Author;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@NoArgsConstructor
public class AuthorRowMapper implements RowMapper<Author> {
    private Map<String, String> columnLabels = Map.of(
            "id", "id", "name", "name", "country", "country");

    public AuthorRowMapper(String alias) {
        String prefix = alias + ".";
        addPrefixToColumnLabels(prefix);
    }

    @Override
    public Author mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        var id = resultSet.getLong(columnLabels.get("id"));
        var name = resultSet.getString(columnLabels.get("name"));
        var country = resultSet.getString(columnLabels.get("country"));
        return new Author(id, name, country);
    }

    private void addPrefixToColumnLabels(String prefix) {
        for (var entry : columnLabels.entrySet()) {
            String value = entry.getValue();
            entry.setValue(prefix + value);
        }
    }
}
