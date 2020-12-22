package com.etn319.dao.mongo;

import com.etn319.model.Book;
import com.etn319.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@EnableMongoRepositories
@Import(GenreMongoRepositoryCustomImpl.class)
public class GenreMongoRepositoryTest {
    private static final long INITIAL_COUNT = 0L;
    @Autowired
    private GenreMongoRepositoryCustom dao;
    @Autowired
    private MongoOperations template;

    @Test
    @DirtiesContext
    void saveBookWithNewGenre_ShouldAffectGenreCount() {
        var book = new Book("Title", null, new Genre("Genre"));
        template.save(book);
        List<Genre> all = dao.findAll();
        System.out.println(all);
        assertThat(dao.count()).isEqualTo(INITIAL_COUNT + 1);
        assertThat(all).hasSize((int) INITIAL_COUNT + 1);
    }

    @Test
    @DirtiesContext
    void saveBookWithNullGenre_ShouldNotAffectGenreCount() {
        var book = new Book("Title", null, null);
        template.save(book);
        List<Genre> all = dao.findAll();
        System.out.println(all);
        assertThat(dao.count()).isEqualTo(INITIAL_COUNT);
        assertThat(all).hasSize((int) INITIAL_COUNT);
    }

    @Test
    @DirtiesContext
    void saveTwoBookWithSameGenre_ShouldAffectGenreCountOnlyOnce() {
        template.save(new Book("Title1", null, new Genre("Genre")));
        template.save(new Book("Title2", null, new Genre("Genre")));
        List<Genre> all = dao.findAll();
        System.out.println(all);
        assertThat(dao.count()).isEqualTo(INITIAL_COUNT + 1);
        assertThat(all).hasSize((int) INITIAL_COUNT + 1);
    }

    @Test
    @DirtiesContext
    void deleteTheOnlyBookWithGenre_ShouldDecreaseGenreCount() {
        var book = template.save(new Book("Title1", null, new Genre("Genre")));
        List<Genre> beforeRemoval = dao.findAll();
        System.out.println(beforeRemoval);

        template.remove(book);
        List<Genre> afterRemoval = dao.findAll();
        System.out.println(afterRemoval);
        assertThat(dao.count()).isEqualTo(INITIAL_COUNT);
        assertThat(afterRemoval).hasSize((int) INITIAL_COUNT);
    }

    @Test
    @DirtiesContext
    void findByTitle_ShouldReturnExistingGenre() {
        var title = "Genre";
        template.save(new Book("Book", null, new Genre(title)));
        Optional<Genre> genre = dao.findByTitle(title);
        assertThat(genre)
                .isPresent().get()
                .extracting(Genre::getTitle)
                .isEqualTo(title);
    }
}
