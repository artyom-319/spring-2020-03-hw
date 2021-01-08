package com.etn319.web.dto.mappers;

import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.web.dto.CommentDto;

public class CommentMapper {
    public static CommentDto toDto(Comment domainObject) {
        return CommentDto.builder()
                .id(domainObject.getId())
                .text(domainObject.getText())
                .commenter(domainObject.getCommenter())
                .bookId(domainObject.getBook() == null ? null : domainObject.getBook().getId())
                .build();
    }

    public static Comment toDomainObject(CommentDto dto) {
        Book book = null;
        String bookId = dto.getBookId();
        if (bookId != null && !bookId.isBlank()) {
            book = new Book();
            book.setId(bookId);
        }
        return new Comment(dto.getText(), dto.getCommenter(), book);
    }
}
