package com.etn319.dao.jdbc.mappers;

import com.etn319.model.Genre;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class GenreRowMapper implements RowMapper<Genre> {
    private Map<String, String> columnLabels = Map.of("id", "id", "title", "title");

    public GenreRowMapper(String prefix, String delimiter) {
        addPrefixToColumnLabels(prefix + delimiter);
    }

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id = rs.getLong(columnLabels.get("id"));
        var title = rs.getString(columnLabels.get("title"));
        return new Genre(id, title);
    }

    private void addPrefixToColumnLabels(String prefix) {
        Map<String, String> columnLabelsWithPrefix = new HashMap<>();
        columnLabels.forEach((k, v) -> columnLabelsWithPrefix.put(k, prefix + v));
        columnLabels = columnLabelsWithPrefix;
    }
}
