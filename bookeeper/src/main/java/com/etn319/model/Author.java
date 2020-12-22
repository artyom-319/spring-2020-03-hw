package com.etn319.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("authors")
@Data
@NoArgsConstructor
public class Author {
    @Id
    private String _id;

    @Field("name")
    @Indexed(unique = true)
    private String name;

    @Field("country")
    private String country;

    private List<Book> books;

    public Author(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public Author(String _id, String name, String country) {
        this._id = _id;
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + _id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
