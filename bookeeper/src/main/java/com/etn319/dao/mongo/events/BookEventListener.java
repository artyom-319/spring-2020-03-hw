package com.etn319.dao.mongo.events;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEventListener extends AbstractMongoEventListener<Book> {
    private final MongoOperations template;

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Book> event) {
        super.onBeforeDelete(event);
        Document source = event.getSource();
        Object bookId = source.get("_id");
        if (bookId != null) {
            Query query = Query.query(Criteria.where("book.$id").is(bookId));
            List<Comment> removed = template.findAllAndRemove(query, Comment.class);
            log.info("Deleted {} comments", removed.size());
        }
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Book> event) {
        super.onBeforeConvert(event);
        Book source = event.getSource();
        Author author = source.getAuthor();
        if (author != null) {
            Author saved = template.save(author);
            log.info("Saved author id={}", saved.getId());
        }
    }
}
