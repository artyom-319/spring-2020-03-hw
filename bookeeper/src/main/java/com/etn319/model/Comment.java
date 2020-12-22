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
    private String _id;

    @Field("commenter")
    private String commenter;

    @Field("text")
    private String text;

    @DBRef
    private Book book;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + _id +
                ", commenter='" + commenter + '\'' +
                ", text='" + text + '\'' +
                ", book name='" + (book == null ? null : book.getTitle()) + '\'' +
                '}';
    }
}
