package com.etn319.web.dto;

import com.etn319.model.Genre;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreDto {
    private String title;

    public Genre toDao() {
        return new Genre(title);
    }

    public static GenreDto ofDao(Genre dao) {
        return GenreDto.builder()
                .title(dao.getTitle())
                .build();
    }
}
