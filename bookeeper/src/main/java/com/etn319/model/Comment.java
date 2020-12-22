package com.etn319.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;

@Entity
@Document("comments")
@Table(name = "comments")
@Data
@NoArgsConstructor
@NamedEntityGraph(name = Comment.FETCH_GRAPH_NAME, attributeNodes = @NamedAttributeNode("book"))
public class Comment {
    public static final String FETCH_GRAPH_NAME = "comment-fetchgraph";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @org.springframework.data.annotation.Id
    private String _id;

    @Column(name = "commenter")
    @Field("commenter")
    private String commenter;

    @Column(name = "text")
    @Field("text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "book_id")
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
