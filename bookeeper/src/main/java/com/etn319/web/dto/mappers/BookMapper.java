package com.etn319.web.dto.mappers;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import com.etn319.web.dto.BookDto;

public class BookMapper {
    public static BookDto toDto(Book domainObject) {
        return BookDto.builder()
                .id(domainObject.getId())
                .title(domainObject.getTitle())
                .genreTitle(domainObject.getGenre() == null ? null : domainObject.getGenre().getTitle())
                .authorId(domainObject.getAuthor() == null ? null : domainObject.getAuthor().getId())
                .authorName(domainObject.getAuthor() == null ? null : domainObject.getAuthor().getName())
                .build();
    }

    public static Book toDomainObject(BookDto dto) {
        Author author = null;
        Genre genre = null;
        String authorId = dto.getAuthorId();
        if (authorId != null && !authorId.isBlank()) {
            author = new Author(authorId, dto.getAuthorName(), null);
        }
        String genreTitle = dto.getGenreTitle();
        if (genreTitle != null && !genreTitle.isBlank()) {
            genre = new Genre(genreTitle);
        }
        return new Book(dto.getId(), dto.getTitle(), author, genre);
    }
}
