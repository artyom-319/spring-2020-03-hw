package com.etn319.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("books")
@Data
@NoArgsConstructor
public class Book {
    @Id
    private String _id;

    @Field("title")
    private String title;

    @DBRef
    @Field("author")
    private Author author;

    @Field("genre")
    private Genre genre;

    @Transient
    private List<Comment> comments;

    public Book(String title, Author author, Genre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public Book(String _id, String title, Author author, Genre genre) {
        this._id = _id;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + _id +
                ", title='" + title + '\'' +
                ", author=" + (author == null ? null : author.getName()) +
                ", genre=" + (genre == null ? null : genre.getTitle()) +
                '}';
    }
}
