package com.etn319.web.dto;

import com.etn319.model.Comment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
    private String id;
    private String text;
    private String commenter;
    private BookDto book;

    public Comment toDao() {
        //todo: comment model constructor
        return new Comment();
    }

    public static CommentDto ofDao(Comment dao) {
        return CommentDto.builder()
                .id(dao.getId())
                .text(dao.getText())
                .commenter(dao.getCommenter())
                .book(BookDto.ofDao(dao.getBook()))
                .build();
    }
}
