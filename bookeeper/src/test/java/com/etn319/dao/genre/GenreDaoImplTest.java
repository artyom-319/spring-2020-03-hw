package com.etn319.dao.genre;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.model.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@JdbcTest
@DisplayName("Genre DAO")
@Import(GenreDaoImpl.class)
class GenreDaoImplTest {
    private static final int INITIAL_COUNT = 2;
    private static final String NEW_TITLE = "Science Fiction";
    private static final String TITLE_1 = "Novel";
    private static final String TITLE_2 = "Drama";
    private static final long INCORRECT_ID = 0L;

    @Autowired
    private GenreDao dao;

    @Test
    @DisplayName("count должен возвращать стартовое количество записей")
    void count() {
        int cnt = dao.count();
        assertThat(cnt).isEqualTo(INITIAL_COUNT);
    }

    @Test
    @DisplayName("insert должен увеличивать число записей на 1")
    void insertAffectsCount() {
        int countBefore = dao.count();
        Genre genre = new Genre(NEW_TITLE);
        dao.insert(genre);
        int countAfter = dao.count();

        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    @DisplayName("insert должен возвращать жанр с теми же данными")
    void insertReturnValue() {
        var genre = new Genre(NEW_TITLE);
        var insertedGenre = dao.insert(genre);

        assertThat(insertedGenre)
                .isNotNull()
                .extracting(Genre::getTitle)
                .isEqualTo(NEW_TITLE);
    }

    @Test
    @DisplayName("insert должен возвращать жанр с новым id")
    void insertGeneratesId() {
        var genre = new Genre(NEW_TITLE);
        var insertedGenre = dao.insert(genre);

        assertThat(insertedGenre.getId()).isNotEqualTo(0L);
    }

    @Test
    @DisplayName("getById должен находить жанр по существующему id")
    void getById() {
        Genre genre = dao.getById(1);
        assertThat(genre)
                .isNotNull()
                .extracting(Genre::getId, Genre::getTitle)
                .containsExactly(1L, TITLE_1);
    }

    @Test
    @DisplayName("getById по несуществующему id должен бросать исключение")
    void getByNotExistingId() {
        Throwable thrown = catchThrowable(() -> dao.getById(INCORRECT_ID));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
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
    @DisplayName("update должен возвращать изменённый жанр с тем же id")
    void update() {
        Genre genre = dao.getById(2L);
        genre.setTitle(NEW_TITLE);
        Genre updated = dao.update(genre);

        assertThat(updated).extracting(Genre::getId, Genre::getTitle)
                .containsExactly(2L, NEW_TITLE);
    }

    @Test
    @DisplayName("update по несуществующему жанру должен бросать исключение")
    void updateByNotExistingId() {
        var genre = new Genre(0L, NEW_TITLE);
        Throwable thrown = catchThrowable(() -> dao.update(genre));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getById после update должен возвращать обновлённый жанр")
    void getByIdUpdatedGenre() {
        var genre = dao.getById(2L);
        genre.setTitle(NEW_TITLE);
        var updatedGenre = dao.update(genre);
        var foundGenre = dao.getById(2L);

        assertThat(foundGenre).isEqualToComparingFieldByField(updatedGenre);
    }

    @Test
    @DisplayName("getById после insert должен возвращать новый жанр")
    void getByIdInsertedGenre() {
        var genre = new Genre(NEW_TITLE);
        var insertedGenre = dao.insert(genre);
        var foundGenre = dao.getById(insertedGenre.getId());

        assertThat(foundGenre)
                .isNotNull()
                .isEqualToComparingFieldByField(insertedGenre);
    }

    @Test
    @DisplayName("delete должен уменьшать count на единицу")
    void deleteByIdAffectsCount() {
        int countBefore = dao.count();
        dao.deleteById(1L);
        int countAfter = dao.count();

        assertThat(countAfter).isEqualTo(countBefore - 1);
    }

    @Test
    @DisplayName("getById после delete должен бросать исключение")
    void getByIdDeletedGenre() {
        dao.deleteById(1L);

        Throwable thrown = catchThrowable(() -> dao.getById(1L));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }
}
