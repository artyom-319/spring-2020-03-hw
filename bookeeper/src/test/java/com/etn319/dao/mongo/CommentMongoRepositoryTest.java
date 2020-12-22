package com.etn319.dao.mongo;

import com.etn319.model.Book;
import com.etn319.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@EnableMongoRepositories
public class CommentMongoRepositoryTest {
    @Autowired
    private CommentMongoRepository dao;
    @Autowired
    private MongoOperations template;

    @Test
    @DirtiesContext
    void bookRemoval_ShouldDeleteRelatedComment() {
        var book = template.save(new Book("Book Title", null, null));
        var comment = new Comment();
        comment.setText("Text");
        comment.setBook(book);
        var commentBefore = template.save(comment);
        assertThat(dao.findById(commentBefore.get_id())).isPresent();

        template.remove(book);
        Optional<Comment> commentAfter = dao.findById(commentBefore.get_id());
        commentAfter.ifPresent(System.out::println);
        assertThat(commentAfter).isEmpty();
    }
}
