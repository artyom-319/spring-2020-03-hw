package com.etn319.dao.jpa;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.datajpa.AuthorRepository;
import com.etn319.model.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataJpaTest
@DisplayName("Author DAO")
class AuthorDaoImplTest {
    private static final int INITIAL_COUNT = 2;
    private static final String NEW_NAME = "Joseph Heller";
    private static final String NEW_COUNTRY = "USA";
    private static final String NAME_1 = "Jack London";
    private static final String COUNTRY_1 = "USA";
    private static final String NAME_2 = "Erich Maria Remarque";
    private static final String COUNTRY_2 = "Germany";
    private static final long ZERO_ID = 0L;
    private static final long NOT_EXISTING_ID = 100L;

    @Autowired
    private AuthorRepository dao;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("count должен возвращать стартовое количество записей")
    void count() {
        long cnt = dao.count();
        assertThat(cnt).isEqualTo(INITIAL_COUNT);
    }

    @Test
    @DisplayName("insert должен увеличивать число записей на 1")
    void saveNewEntityAffectsCount() {
        long countBefore = dao.count();
        Author author = new Author(NEW_NAME, NEW_COUNTRY);
        dao.save(author);
        long countAfter = dao.count();

        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    @DisplayName("save нового автора должен возвращать автора с теми же данными")
    void saveNewEntityReturnValue() {
        var author = new Author(NEW_NAME, NEW_COUNTRY);
        var insertedAuthor = dao.save(author);

        assertThat(insertedAuthor)
                .isNotNull()
                .extracting(Author::getName, Author::getCountry)
                .containsExactly(NEW_NAME, NEW_COUNTRY);
    }

    @Test
    @DisplayName("insert должен возвращать автора с новым id")
    void saveForNewEntityGeneratesId() {
        var author = new Author(NEW_NAME, NEW_COUNTRY);
        var insertedAuthor = dao.save(author);

        assertThat(insertedAuthor.getId()).isNotEqualTo(0L);
    }

    @Test
    @DisplayName("findById должен находить автора по существующему id")
    void getById() {
        Optional<Author> author = dao.findById(1L);
        assertThat(author).isPresent();
        assertThat(author.orElseThrow())
                .extracting(Author::getId, Author::getName, Author::getCountry)
                .containsExactly(1L, NAME_1, COUNTRY_1);
    }

    @Test
    @DisplayName("findById по несуществующему id должен возвращать пустой Optional")
    void getByNotExistingId() {
        Optional<Author> author = dao.findById(ZERO_ID);
        assertThat(author).isEmpty();
    }

    @Test
    @DisplayName("findAll должен возвращать все объекты в таблице")
    void getAll() {
        List<Author> authors = dao.findAll();

        assertThat(authors)
                .hasSize(INITIAL_COUNT)
                .flatExtracting(Author::getName, Author::getCountry)
                .containsExactly(NAME_1, COUNTRY_1, NAME_2, COUNTRY_2);
    }

    @Test
    @DisplayName("update должен возвращать изменённого автора с тем же id")
    void updateReturnValue() {
        Author author = em.find(Author.class, 2L);
        author.setName(NEW_NAME);
        author.setCountry(NEW_COUNTRY);
        Author updated = dao.save(author);

        assertThat(updated)
                .extracting(Author::getId, Author::getName, Author::getCountry)
                .containsExactly(2L, NEW_NAME, NEW_COUNTRY);
    }

    @Test
    @DisplayName("update по несуществующему автору должен бросать исключение")
    void updateByNotExistingId() {
        var author = new Author(NOT_EXISTING_ID, NEW_NAME, NEW_COUNTRY);
        Throwable thrown = catchThrowable(() -> dao.save(author));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("findById после update должен возвращать обновлённого автора")
    void getByIdUpdatedAuthor() {
        var author = em.find(Author.class, 2L);
        author.setName(NEW_NAME);
        author.setCountry(NEW_COUNTRY);
        var updatedAuthor = dao.save(author);
        var foundAuthor = em.find(Author.class, 2L);

        assertThat(foundAuthor).isEqualToComparingFieldByField(updatedAuthor);
    }

    @Test
    @DisplayName("findById после insert должен возвращать нового автора")
    void getByIdInsertedAuthor() {
        var author = new Author(NEW_NAME, NEW_COUNTRY);
        var insertedAuthor = dao.save(author);
        var foundAuthor = em.find(Author.class, insertedAuthor.getId());

        assertThat(foundAuthor)
                .isNotNull()
                .isEqualToComparingFieldByField(insertedAuthor);
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
    @DisplayName("findById после delete должен возвращать пустой Optional")
    void getByIdDeletedAuthor() {
        dao.deleteById(1L);

        var author = em.find(Author.class, 1L);
        assertThat(author).isNull();
    }
}
