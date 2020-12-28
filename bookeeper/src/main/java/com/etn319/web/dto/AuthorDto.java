package com.etn319.web.dto;

import com.etn319.model.Author;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDto {
    private String id;
    private String name;
    private String country;

    public Author toDao() {
        return new Author(id, name, country);
    }

    public static AuthorDto ofDao(Author dao) {
        return AuthorDto.builder()
                .id(dao.getId())
                .name(dao.getName())
                .country(dao.getCountry())
                .build();
    }
}
