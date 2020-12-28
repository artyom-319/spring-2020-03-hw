package com.etn319.web.dto;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String id;
    private String title;
    private String authorId;
    private String authorName;
    private String genreTitle;

    public Book toDao() {
        return new Book(id, title, new Author(authorId, authorName, null), new Genre(genreTitle));
    }

    public static BookDto ofDao(Book dao) {
        return BookDto.builder()
                .id(dao.getId())
                .title(dao.getTitle())
                .genreTitle(dao.getGenre() == null ? null : dao.getGenre().getTitle())
                .authorId(dao.getAuthor() == null ? null : dao.getAuthor().getId())
                .authorName(dao.getAuthor() == null ? null : dao.getAuthor().getName())
                .build();
    }
}
