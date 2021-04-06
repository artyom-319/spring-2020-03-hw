package com.etn319.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("comments")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    private String id;

    @Field("commenter")
    @DBRef
    private ServiceUser commenter;

    @Field("text")
    private String text;

    @DBRef
    private Book book;

    public Comment(String text, ServiceUser commenter, Book book) {
        this.commenter = commenter;
        this.text = text;
        this.book = book;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", commenter='" + (commenter == null ? null : commenter.getName()) + '\'' +
                ", text='" + text + '\'' +
                ", book name='" + (book == null ? null : book.getTitle()) + '\'' +
                '}';
    }
}
