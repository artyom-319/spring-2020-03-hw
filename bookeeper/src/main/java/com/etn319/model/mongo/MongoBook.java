package com.etn319.model.mongo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("books")
@Data
@NoArgsConstructor
public class MongoBook {

    @Id
    private String id;

    @Field("title")
    private String title;

    @DBRef
    @Field("author")
    private MongoAuthor author;

    @Field("genre")
    private MongoGenre genre;

    @Field("comments")
    private List<MongoComment> comments;

    public MongoBook(String title, MongoAuthor author, MongoGenre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public MongoBook(String id, String title, MongoAuthor author, MongoGenre genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author=" + (author == null ? null : author.getName()) +
                ", genre=" + (genre == null ? null : genre.getTitle()) +
                '}';
    }
}
