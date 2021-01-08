package com.etn319.web.dto;

import com.etn319.model.Book;
import com.etn319.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String id;
    private String text;
    private String commenter;
    private String bookId;

    public Comment toDao() {
        Book book = null;
        if (bookId != null && !bookId.isBlank()) {
            book = new Book();
            book.setId(bookId);
        }
        return new Comment(text, commenter, book);
    }

    public static CommentDto ofDao(Comment dao) {
        return CommentDto.builder()
                .id(dao.getId())
                .text(dao.getText())
                .commenter(dao.getCommenter())
                .bookId(dao.getBook() == null ? null : dao.getBook().getId())
                .build();
    }
}
