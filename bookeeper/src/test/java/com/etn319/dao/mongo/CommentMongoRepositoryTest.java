package com.etn319.dao.mongo;

import com.etn319.dao.mongo.events.BookEventListener;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@EnableMongoRepositories
@Import(BookEventListener.class)
public class CommentMongoRepositoryTest {
    @Autowired
    private CommentMongoRepository dao;
    @Autowired
    private MongoOperations template;

    @Test
    @DirtiesContext
    @DisplayName("При удалении книги комментарии к ней также должны удаляться")
    void bookRemoval_ShouldDeleteRelatedComment() {
        var book = template.save(new Book("Book Title", null, null));
        var comment = new Comment();
        comment.setText("Text");
        comment.setBook(book);
        var commentBefore = template.save(comment);
        assertThat(dao.findById(commentBefore.getId())).isPresent();

        template.remove(book);
        Optional<Comment> commentAfter = dao.findById(commentBefore.getId());
        commentAfter.ifPresent(System.out::println);
        assertThat(commentAfter).isEmpty();
    }
}
