package com.etn319.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = Comment.FETCH_GRAPH_NAME, attributeNodes = @NamedAttributeNode("book"))
public class Comment {
    public static final String FETCH_GRAPH_NAME = "comment-fetchgraph";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "commenter")
    private String commenter;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", commenter='" + commenter + '\'' +
                ", text='" + text + '\'' +
                ", book name='" + (book == null ? null : book.getTitle()) + '\'' +
                '}';
    }
}
