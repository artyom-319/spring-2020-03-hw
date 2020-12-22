package com.etn319.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("genres")
@Data
@NoArgsConstructor
public class Genre {
    @Id
    private String _id;

    private String title;

    @Transient
    private List<Book> books;

    public Genre(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "title='" + title + '\'' +
                '}';
    }
}
