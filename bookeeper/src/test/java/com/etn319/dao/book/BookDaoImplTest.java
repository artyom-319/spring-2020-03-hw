package com.etn319.dao.book;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.author.AuthorDao;
import com.etn319.dao.author.AuthorDaoImpl;
import com.etn319.dao.genre.GenreDao;
import com.etn319.dao.genre.GenreDaoImpl;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@JdbcTest
@DisplayName("Book DAO")
@Import({BookDaoImpl.class, GenreDaoImpl.class, AuthorDaoImpl.class})
class BookDaoImplTest {
    private static final int INITIAL_COUNT = 3;
    private static final int NOVELS_COUNT = 3;
    private static final int LONDON_BOOKS_COUNT = 2;
    private static final String NEW_TITLE = "The Night in Lisbon";
    private static final String TITLE_1 = "Martin Eden";
    private static final String TITLE_2 = "Three Comrades";
    private static final String TITLE_3 = "Sea Wolf";
    private static final long INCORRECT_ID = 0L;

    private Author authorRemarque;
    private Author authorLondon;
    private Genre genreNovel;
    private Genre genreDrama;

    @Autowired
    private BookDao dao;
    @Autowired
    private AuthorDao authorDao;
    @Autowired
    private GenreDao genreDao;

    @BeforeEach
    public void setUp() {
        authorLondon = authorDao.getById(1L);
        authorRemarque = authorDao.getById(2L);
        genreNovel = genreDao.getById(1L);
        genreDrama = genreDao.getById(2L);
    }

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
        Book book = new Book(NEW_TITLE, authorRemarque, genreNovel);
        dao.insert(book);
        int countAfter = dao.count();

        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    @DisplayName("insert должен возвращать книгу с теми же данными")
    void insertReturnValue() {
        var book = new Book(NEW_TITLE, authorRemarque, genreNovel);
        var insertedBook = dao.insert(book);

        assertThat(insertedBook)
                .isNotNull()
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly(NEW_TITLE, authorRemarque, genreNovel);
    }

    @Test
    @DisplayName("insert должен возвращать книгу с новым id")
    void insertGeneratesId() {
        var book = new Book(NEW_TITLE, authorRemarque, genreNovel);
        var insertedBook = dao.insert(book);

        assertThat(insertedBook.getId()).isNotEqualTo(0L);
    }

    @Test
    @DisplayName("getById должен находить книгу по существующему id")
    void getById() {
        Book book = dao.getById(1);
        assertThat(book)
                .isNotNull()
                .extracting(Book::getId, Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly(1L, TITLE_1, authorLondon, genreNovel);
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
        List<Book> books = dao.getAll();

        assertThat(books)
                .hasSize(INITIAL_COUNT)
                .flatExtracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly(
                        TITLE_1, authorLondon, genreNovel,
                        TITLE_2, authorRemarque, genreNovel,
                        TITLE_3, authorLondon, genreNovel
                );
    }

    @Test
    @DisplayName("update должен возвращать изменённую книгу с тем же id")
    void update() {
        Book book = dao.getById(2L);
        book.setTitle(NEW_TITLE);
        book.setAuthor(authorLondon);
        book.setGenre(genreDrama);
        Book updated = dao.update(book);

        assertThat(updated)
                .extracting(Book::getId, Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly(2L, NEW_TITLE, authorLondon, genreDrama);
    }

    @Test
    @DisplayName("update по несуществующей книге должен бросать исключение")
    void updateByNotExistingId() {
        var book = new Book(INCORRECT_ID, NEW_TITLE, authorRemarque, genreNovel);
        Throwable thrown = catchThrowable(() -> dao.update(book));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getById после update должен возвращать обновлённую книгу")
    void getByIdUpdatedBook() {
        var book = dao.getById(2L);
        book.setTitle(NEW_TITLE);
        book.setAuthor(authorLondon);
        book.setGenre(genreDrama);
        var updatedGenre = dao.update(book);
        var foundGenre = dao.getById(2L);

        assertThat(foundGenre).isEqualToComparingFieldByField(updatedGenre);
    }

    @Test
    @DisplayName("getById после insert должен возвращать новую книгу")
    void getByIdInsertedBook() {
        var book = new Book(NEW_TITLE, authorRemarque, genreNovel);
        var insertedBook = dao.insert(book);
        var foundBook = dao.getById(insertedBook.getId());

        assertThat(foundBook)
                .isNotNull()
                .isEqualToComparingFieldByField(insertedBook);
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
    void getByIdDeletedBook() {
        dao.deleteById(1L);

        Throwable thrown = catchThrowable(() -> dao.getById(1L));
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getByGenre должен вернуть книги только одного жанра")
    void getByGenre() {
        List<Book> novels = dao.getByGenre(genreNovel);
        assertThat(novels).hasSize(NOVELS_COUNT);
        assertThat(novels)
                .flatExtracting(Book::getGenre)
                .hasSameElementsAs(Collections.nCopies(NOVELS_COUNT, genreNovel));
    }

    @Test
    @DisplayName("getByGenreId по несуществующему id должен вернуть пустой список")
    void getByIncorrectGenreId() {
        List<Book> books = dao.getByGenreId(INCORRECT_ID);
        assertThat(books).isEmpty();
    }

    @Test
    @DisplayName("getByAuthor должен вернуть книги только одного автора")
    void getByAuthor() {
        List<Book> booksOfLondon = dao.getByAuthor(authorLondon);
        assertThat(booksOfLondon).hasSize(LONDON_BOOKS_COUNT);
        assertThat(booksOfLondon)
                .flatExtracting(Book::getAuthor)
                .hasSameElementsAs(Collections.nCopies(LONDON_BOOKS_COUNT, authorLondon));
    }

    @Test
    @DisplayName("getByAuthorId по несуществующему id должен вернуть пустой список")
    void getByIncorrectAuthorId() {
        List<Book> books = dao.getByAuthorId(INCORRECT_ID);
        assertThat(books).isEmpty();
    }
}
