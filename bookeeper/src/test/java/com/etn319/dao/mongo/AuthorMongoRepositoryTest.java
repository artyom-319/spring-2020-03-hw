package com.etn319.dao.mongo;

import com.etn319.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataMongoTest
@EnableMongoRepositories
public class AuthorMongoRepositoryTest {
    private static final long INITIAL_COUNT = 0L;

    @Autowired
    private AuthorMongoRepository dao;

    @Test
    @DirtiesContext
    void saveNewEntity__shouldAffectCount() {
        var author = new Author("Name", "Country");
        dao.save(author);
        long resultCount = dao.count();
        assertThat(resultCount).isEqualTo(INITIAL_COUNT + 1);
    }

    @Test
    @DirtiesContext
    void saveAuthorWithDuplicateName__shouldThrowException() {
        var author1 = new Author("Name", "Country 1");
        var author2 = new Author("Name", "Country 2");
        dao.save(author1);
        Throwable t = catchThrowable(() -> dao.save(author2));

        assertThat(t).isInstanceOf(DuplicateKeyException.class);
    }
}
