package com.etn319.web.dto;

import com.etn319.model.Book;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookDto {
    private String id;
    private String title;
    private AuthorDto author;
    private GenreDto genre;

    public Book toDao() {
        return new Book(id, title, author.toDao(), genre.toDao());
    }

    public static BookDto ofDao(Book dao) {
        return BookDto.builder()
                .id(dao.getId())
                .title(dao.getTitle())
                .genre(GenreDto.ofDao(dao.getGenre()))
                .author(AuthorDto.ofDao(dao.getAuthor()))
                .build();
    }
}
