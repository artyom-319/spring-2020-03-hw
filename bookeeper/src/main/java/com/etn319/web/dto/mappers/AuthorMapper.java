package com.etn319.web.dto.mappers;

import com.etn319.model.Author;
import com.etn319.web.dto.AuthorDto;

public class AuthorMapper {
    public static AuthorDto toDto(Author domainObject) {
        return AuthorDto.builder()
                .id(domainObject.getId())
                .name(domainObject.getName())
                .country(domainObject.getCountry())
                .build();
    }

    public static Author toDomainObject(AuthorDto dto) {
        return new Author(dto.getId(), dto.getName(), dto.getCountry());
    }
}
