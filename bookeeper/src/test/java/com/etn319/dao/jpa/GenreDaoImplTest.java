package com.etn319.dao.jpa;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.GenreDao;
import com.etn319.model.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataJpaTest
@DisplayName("Genre DAO")
@Import(GenreDaoJpaImpl.class)
class GenreDaoImplTest {
    private static final int INITIAL_COUNT = 2;
    private static final String NEW_TITLE = "Science Fiction";
    private static final String TITLE_1 = "Novel";
    private static final String TITLE_2 = "Drama";
    private static final long ZERO_ID = 0L;
    private static final long NOT_EXISTING_ID = 1000L;

    @Autowired
    private GenreDao dao;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("count должен возвращать стартовое количество записей")
    void count() {
        long cnt = dao.count();
        assertThat(cnt).isEqualTo(INITIAL_COUNT);
    }

    @Test
    @DisplayName("save для нового жанра должен увеличивать число записей на 1")
    void saveForNewEntityAffectsCount() {
        long countBefore = dao.count();
        Genre genre = new Genre(NEW_TITLE);
        dao.save(genre);
        long countAfter = dao.count();

        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    @DisplayName("save должен возвращать жанр с теми же данными")
    void saveNewEntityReturnValue() {
        var genre = new Genre(NEW_TITLE);
        var insertedGenre = dao.save(genre);

        assertThat(insertedGenre)
                .isNotNull()
                .extracting(Genre::getTitle)
                .isEqualTo(NEW_TITLE);
    }

    @Test
    @DisplayName("save нового жанра должен возвращать жанр со сгенерированным id != 0")
    void insertGeneratesId() {
        var genre = new Genre(NEW_TITLE);
        var insertedGenre = dao.save(genre);

        assertThat(insertedGenre.getId()).isNotEqualTo(0L);
    }

    @Test
    @DisplayName("getById должен находить жанр по существующему id")
    void getById() {
        Optional<Genre> genre = dao.getById(1);

        assertThat(genre)
                .isPresent();
        assertThat(genre.orElseThrow())
                .extracting(Genre::getId, Genre::getTitle)
                .containsExactly(1L, TITLE_1);
    }

    @Test
    @DisplayName("getById по несуществующему id должен возвращать пустой Optional")
    void getByNotExistingId() {
        Optional<Genre> genre = dao.getById(ZERO_ID);
        assertThat(genre).isEmpty();
    }

    @Test
    @DisplayName("getAll должен возвращать все объекты в таблице")
    void getAll() {
        List<Genre> genres = dao.getAll();

        assertThat(genres)
                .hasSize(INITIAL_COUNT)
                .flatExtracting(Genre::getTitle)
                .containsExactly(TITLE_1, TITLE_2);
    }

    @Test
    @DisplayName("save для существующего жанра должен возвращать изменённый жанр с тем же id")
    void update() {
        Genre genre = em.find(Genre.class, 2L);
        genre.setTitle(NEW_TITLE);
        Genre updated = dao.save(genre);

        assertThat(updated).extracting(Genre::getId, Genre::getTitle)
                .containsExactly(2L, NEW_TITLE);
    }

    @Test
    @DisplayName("update по несуществующему жанру должен бросать исключение")
    void updateByNotExistingId() {
        var genre = new Genre(NOT_EXISTING_ID, NEW_TITLE);
        Throwable thrown = catchThrowable(() -> dao.save(genre));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("em.find после update должен возвращать обновлённый жанр")
    void getByIdUpdatedGenre() {
        var genre = em.find(Genre.class, 2L);
        genre.setTitle(NEW_TITLE);
        var updatedGenre = dao.save(genre);
        var foundGenre = em.find(Genre.class, 2L);

        assertThat(foundGenre).isEqualToComparingFieldByField(updatedGenre);
    }

    @Test
    @DisplayName("em.find после insert должен возвращать новый жанр")
    void getByIdInsertedGenre() {
        var genre = new Genre(NEW_TITLE);
        var insertedGenre = dao.save(genre);
        var foundGenre = em.find(Genre.class, insertedGenre.getId());

        assertThat(foundGenre).isNotNull();
        assertThat(foundGenre)
                .isEqualToComparingFieldByField(insertedGenre);
    }

    @Test
    @DisplayName("delete должен уменьшать count на единицу")
    void deleteByIdAffectsCount() {
        long countBefore = dao.count();
        dao.deleteById(1L);
        long countAfter = dao.count();

        assertThat(countAfter).isEqualTo(countBefore - 1);
    }

    @Test
    @DisplayName("getById после delete должен возвращать пустой Optional")
    void getByIdDeletedGenre() {
        dao.deleteById(1L);
        Optional<Genre> genre = dao.getById(1L);

        assertThat(genre).isEmpty();
    }
}
