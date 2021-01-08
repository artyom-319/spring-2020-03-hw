package com.etn319.web.dto.mappers;

import com.etn319.model.Genre;
import com.etn319.web.dto.GenreDto;

public class GenreMapper {
    public static GenreDto toDto(Genre domainObject) {
        return GenreDto.builder()
                .title(domainObject.getTitle())
                .build();
    }

    public static Genre toDomainObject(GenreDto dto) {
        return new Genre(dto.getTitle());
    }
}
