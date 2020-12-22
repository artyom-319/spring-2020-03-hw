package com.etn319.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Document("authors")
@Table(name = "authors")
@Data
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @org.springframework.data.annotation.Id
    private String _id;

    @Column(name = "name")
    @Field("name")
    @Indexed(unique = true)
    private String name;

    @Column(name = "country")
    @Field("country")
    private String country;

    @OneToMany(mappedBy = "author")
    private List<Book> books;

    public Author(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public Author(long id, String name, String country) {
        this.id = id;
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
