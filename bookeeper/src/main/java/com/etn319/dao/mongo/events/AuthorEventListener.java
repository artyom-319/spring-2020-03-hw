package com.etn319.dao.mongo.events;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorEventListener extends AbstractMongoEventListener<Author> {
    private final MongoOperations template;

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Author> event) {
        super.onBeforeDelete(event);
        Document source = event.getSource();
        Object authorId = source.get("_id");
        if (authorId != null) {
            Query query = Query.query(Criteria.where("author.$id").is(authorId));
            Update update = new Update().unset("author");
            UpdateResult updateResult = template.updateMulti(query, update, Book.class);
            log.info("Modified {} book(s)", updateResult.getModifiedCount());
        }
    }
}
