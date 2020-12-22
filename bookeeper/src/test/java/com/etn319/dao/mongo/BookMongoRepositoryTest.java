package com.etn319.dao.mongo;

import com.etn319.dao.mongo.events.AuthorEventListener;
import com.etn319.dao.mongo.events.BookEventListener;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@DataMongoTest
@EnableMongoRepositories
@Import({BookEventListener.class, AuthorEventListener.class})
public class BookMongoRepositoryTest {
    private static final long INITIAL_COUNT = 0L;
    private static final Genre GENRE = new Genre("Genre");
    private static final Author TRANSIENT_AUTHOR = new Author("Author", "Authorland");

    @Autowired
    private BookMongoRepository dao;
    @Autowired
    private AuthorMongoRepository authorDao;
    @Autowired
    private MongoOperations template;

    @Test
    @DirtiesContext
    @DisplayName("При вставке книги их число должно увеличиваться")
    void saveNewEntity__shouldAffectCount() {
        var book = new Book("Title", null, GENRE);
        dao.save(book);
        long resultCount = dao.count();
        assertThat(resultCount).isEqualTo(INITIAL_COUNT + 1);
    }

    @Test
    @DirtiesContext
    @DisplayName("При сохранении книги с автором, которого нет в базе, автор тоже должен туда записываться")
    void saveNewEntityWithTransientAuthor__shouldSaveBoth() {
        var book = dao.save(new Book("Title", TRANSIENT_AUTHOR, GENRE));
        var author = book.getAuthor();

        assertThat(dao.existsById(book.getId())).isTrue();
        assertThat(authorDao.existsById(author.getId())).isTrue();
    }

    @Test
    @DirtiesContext
    @DisplayName("При сохранение книги вложенный автор тоже должен обновляться")
    void saveEntityWithExistingRelatedOne__shouldUpdateRelated() {
        var author = authorDao.save(TRANSIENT_AUTHOR);
        var book = dao.save(new Book("Title", author, GENRE));

        String updatedName = "updated";
        book.getAuthor().setName(updatedName);
        dao.save(book);
        var updatedAuthor = authorDao.findById(author.getId());

        assertThat(updatedAuthor)
                .isPresent().get()
                .extracting(Author::getName)
                .isEqualTo(updatedName);
    }

    @Test
    @DirtiesContext
    @DisplayName("При удалении автора ссылка на него в книге должна отвязываться")
    void deleteAuthor_ShouldSetNullToBookAuthor() {
        var author = authorDao.save(TRANSIENT_AUTHOR);
        var book = dao.save(new Book("Title", author, GENRE));

        authorDao.delete(author);
        var updatedBook = dao.findById(book.getId()).orElseThrow();
        assertThat(updatedBook)
                .extracting(Book::getAuthor)
                .isNull();

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("author._id").is(author.getId()))
        );
        List<Book> results = template.aggregate(aggregation, Book.class, Book.class).getMappedResults();
        assertThat(results).isEmpty();
    }
}
