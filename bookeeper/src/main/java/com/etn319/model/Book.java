package com.etn319.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Document("books")
@Table(name = "books")
@Data
@NoArgsConstructor
@NamedEntityGraph(
        name = Book.FETCH_GRAPH_NAME,
        attributeNodes = {
                @NamedAttributeNode("author"),
                @NamedAttributeNode("genre")
        }
)
public class Book {
    public static final String FETCH_GRAPH_NAME = "book-fetchgraph";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @org.springframework.data.annotation.Id
    private String _id;

    @Column(name = "title")
    @Field("title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @DBRef
    @Field("author")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    @Field("genre")
    private Genre genre;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Transient
    private List<Comment> comments;

    public Book(String title, Author author, Genre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public Book(long id, String title, Author author, Genre genre) {
        this.id = id;
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
