package com.etn319.web.dto.mappers;

import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.model.ServiceUser;
import com.etn319.web.dto.CommentDto;

public class CommentMapper {
    public static CommentDto toDto(Comment domainObject) {
        return CommentDto.builder()
                .id(domainObject.getId())
                .text(domainObject.getText())
                .commenterId(domainObject.getCommenter() == null ? null : domainObject.getCommenter().getId())
                .commenterName(domainObject.getCommenter() == null ? null : domainObject.getCommenter().getName())
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
        ServiceUser commenter = null;
        String commenterId = dto.getCommenterId();
        if (commenterId != null && !commenterId.isBlank()) {
            commenter = new ServiceUser();
            commenter.setId(commenterId);
            commenter.setName(dto.getCommenterName());
        }

        return new Comment(dto.getText(), commenter, book);
    }
}
