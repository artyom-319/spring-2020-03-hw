package com.etn319.dao.author;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.model.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@JdbcTest
@DisplayName("Author DAO")
@Import(AuthorDaoImpl.class)
class AuthorDaoImplTest {
    private static final int INITIAL_COUNT = 2;
    private static final String NEW_NAME = "Joseph Heller";
    private static final String NEW_COUNTRY = "USA";
    private static final String NAME_1 = "Jack London";
    private static final String COUNTRY_1 = "USA";
    private static final String NAME_2 = "Erich Maria Remarque";
    private static final String COUNTRY_2 = "Germany";
    private static final long INCORRECT_ID = 0L;

    @Autowired
    private AuthorDao dao;

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
        Author author = new Author(NEW_NAME, NEW_COUNTRY);
        dao.insert(author);
        int countAfter = dao.count();

        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    @DisplayName("insert должен возвращать автора с теми же данными")
    void insertReturnValue() {
        var author = new Author(NEW_NAME, NEW_COUNTRY);
        var insertedAuthor = dao.insert(author);

        assertThat(insertedAuthor)
                .isNotNull()
                .extracting(Author::getName, Author::getCountry)
                .containsExactly(NEW_NAME, NEW_COUNTRY);
    }

    @Test
    @DisplayName("insert должен возвращать автора с новым id")
    void insertGeneratesId() {
        var author = new Author(NEW_NAME, NEW_COUNTRY);
        var insertedAuthor = dao.insert(author);

        assertThat(insertedAuthor.getId()).isNotEqualTo(0L);
    }

    @Test
    @DisplayName("getById должен находить автора по существующему id")
    void getById() {
        Author author = dao.getById(1);
        assertThat(author)
                .isNotNull()
                .extracting(Author::getId, Author::getName, Author::getCountry)
                .containsExactly(1L, NAME_1, COUNTRY_1);
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
        List<Author> authors = dao.getAll();

        assertThat(authors)
                .hasSize(INITIAL_COUNT)
                .flatExtracting(Author::getName, Author::getCountry)
                .containsExactly(NAME_1, COUNTRY_1, NAME_2, COUNTRY_2);
    }

    @Test
    @DisplayName("update должен возвращать изменённого автора с тем же id")
    void update() {
        Author author = dao.getById(2L);
        author.setName(NEW_NAME);
        author.setCountry(NEW_COUNTRY);
        Author updated = dao.update(author);

        assertThat(updated).extracting(Author::getId, Author::getName, Author::getCountry)
                .containsExactly(2L, NEW_NAME, NEW_COUNTRY);
    }

    @Test
    @DisplayName("update по несуществующему автору должен бросать исключение")
    void updateByNotExistingId() {
        var author = new Author(0L, NEW_NAME, NEW_COUNTRY);
        Throwable thrown = catchThrowable(() -> dao.update(author));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getById после update должен возвращать обновлённого автора")
    void getByIdUpdatedAuthor() {
        var author = dao.getById(2L);
        author.setName(NEW_NAME);
        author.setCountry(NEW_COUNTRY);
        var updatedAuthor = dao.update(author);
        var foundAuthor = dao.getById(2L);

        assertThat(foundAuthor).isEqualToComparingFieldByField(updatedAuthor);
    }

    @Test
    @DisplayName("getById после insert должен возвращать нового автора")
    void getByIdInsertedAuthor() {
        var author = new Author(NEW_NAME, NEW_COUNTRY);
        var insertedAuthor = dao.insert(author);
        var foundAuthor = dao.getById(insertedAuthor.getId());

        assertThat(foundAuthor)
                .isNotNull()
                .isEqualToComparingFieldByField(insertedAuthor);
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
    void getByIdDeletedAuthor() {
        dao.deleteById(1L);

        Throwable thrown = catchThrowable(() -> dao.getById(1L));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }
}
