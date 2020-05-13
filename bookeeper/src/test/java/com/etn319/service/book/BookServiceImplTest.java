package com.etn319.service.book;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.book.BookDao;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.EmptyCacheException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@SpringBootTest
class BookServiceImplTest {
    private static final int COUNT = -100;
    private static final long INCORRECT_ID = 0L;
    private static final String TITLE = "20 Years Later";
    private static final String NEW_TITLE = "10 Years Later";
    private static final Book BOOK = new Book(1L, TITLE, new Author(), new Genre());

    private final List<Book> allBooks = Collections.nCopies(5, BOOK);
    private final List<Book> booksByGenre = Collections.emptyList();
    private final List<Book> booksByAuthor = Collections.singletonList(BOOK);

    @Configuration
    static class Config {
        @Bean
        public CacheHolder cacheHolder() {
            return new CacheHolder();
        }

        @Bean
        public BookService bookService(BookDao bookDao, CacheHolder cacheHolder) {
            return new BookServiceImpl(bookDao, cacheHolder);
        }
    }

    @MockBean
    private BookDao bookDao;
    @Autowired
    private CacheHolder cacheHolder;
    @Autowired
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        given(bookDao.count()).willReturn(COUNT);
        given(bookDao.getById(anyLong())).willReturn(BOOK);
        given(bookDao.getByAuthor(any(Author.class))).willReturn(booksByAuthor);
        given(bookDao.getByGenreId(anyLong())).willReturn(booksByGenre);
        given(bookDao.getAll()).willReturn(allBooks);
        doNothing().when(bookDao).deleteById(longThat(l -> l != INCORRECT_ID));
        doThrow(EntityNotFoundException.class).when(bookDao).deleteById(INCORRECT_ID);

        bookService.clearCache();
    }

    @Test
    @DisplayName("count должен вызывать метод dao.count и возвращать его результат")
    void count() {
        int cnt = bookService.count();
        verify(bookDao, only()).count();
        assertThat(cnt).isEqualTo(COUNT);
    }

    @Test
    @DisplayName("getById должен вызывать метод dao.getById, возвращать его результат")
    void getByIdDelegatesCallToDao() {
        var id = 1L;
        var book = bookService.getById(id);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(bookDao, only()).getById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);

        assertThat(book).isEqualToComparingFieldByField(BOOK);
    }

    @Test
    @DisplayName("getById должен кэшировать результат")
    void getByIdStoresResultInCache() {
        var id = 1L;
        var book = bookService.getById(id);

        assertThat(bookService)
                .extracting(BookService::getCache)
                .isNotNull()
                .isEqualTo(book);
    }

    @Test
    @DisplayName("getAll должен вызывать dao.getAll и возвращать результат")
    void getAll() {
        List<Book> actual = bookService.getAll();

        verify(bookDao, only()).getAll();
        assertThat(actual).isSameAs(allBooks);
    }

    @Test
    @DisplayName("getAll не должен сохранять объекты в кэше")
    void getAllDoesNotStoreCache() {
        bookService.getAll();
        Throwable thrown = catchThrowable(() -> bookService.getCache());
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("save нового объекта должен вызывать dao.insert, передавать ему значение из кэша " +
            "и не должен вызывать dao.update")
    void saveNewObject() {
        var book = bookService.create(TITLE);
        book.setGenre(new Genre());
        book.setAuthor(new Author());
        bookService.save();
        var argumentCaptor = ArgumentCaptor.forClass(Book.class);

        verify(bookDao, only()).insert(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isSameAs(book);
    }

    @Test
    @DisplayName("save нового объекта должен вызывать dao.update, передавать ему значение из кэша " +
            "и не должен вызывать dao.insert")
    void saveExistingObject() {
        var book = bookService.getById(1L);
        bookService.save();
        var argumentCaptor = ArgumentCaptor.forClass(Book.class);

        verify(bookDao, atLeastOnce()).update(argumentCaptor.capture());
        verify(bookDao, never()).insert(any());
        assertThat(argumentCaptor.getValue()).isSameAs(book);
    }

    @Test
    @DisplayName("save при пустом кэше должен выбросить исключение")
    void saveWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> bookService.save());
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("deleteById должен вызывать dao.deleteById с тем же аргументом и возвращать true при корректном id")
    void deleteById() {
        var correctId = 1L;
        boolean wasDeleted = bookService.deleteById(correctId);
        var argumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(bookDao, only()).deleteById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(correctId);
        assertThat(wasDeleted).isTrue();
    }

    @Test
    @DisplayName("deleteById должен вызывать dao.deleteById с тем же аргументом и возвращать true при корректном id")
    void deleteByIncorrectId() {
        boolean wasDeleted = bookService.deleteById(INCORRECT_ID);
        assertThat(wasDeleted).isFalse();
    }

    @Test
    void getByGenreId() {
        List<Book> booksByGenre = bookService.getByGenreId(1L);
        var argumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(bookDao, only()).getByGenreId(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(1L);
        assertThat(booksByGenre).isSameAs(booksByGenre);
    }

    @Test
    void getByCachedAuthor() {
        var author = new Author(2L, "name", "country");
        cacheHolder.setAuthor(author);
        List<Book> booksByAuthor = bookService.getByCachedAuthor();
        var argumentCaptor = ArgumentCaptor.forClass(Author.class);

        verify(bookDao, only()).getByAuthor(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(author);
        assertThat(booksByAuthor).isSameAs(booksByAuthor);
    }


    @Test
    @DisplayName("create должен создавать объект книги с переданными параметрами")
    void create() {
        var book = bookService.create(TITLE);

        assertThat(book).isNotNull()
                .extracting(Book::getId, Book::getTitle)
                .containsExactly(0L, TITLE);
    }

    @Test
    @DisplayName("create должен складывать в кэш созданный объект книги")
    void createdBookStoredInCache() {
        var book = bookService.create(TITLE);
        var cachedBook = bookService.getCache();

        assertThat(cachedBook).isNotNull()
                .isEqualToComparingFieldByField(book);
    }

    @Test
    @DisplayName("create создаёт книгу без автора и жанра")
    void createdBookDoesNotHaveWiredObjects() {
        var book = bookService.create(TITLE);

        assertThat(book).extracting(Book::getAuthor).isNull();
        assertThat(book).extracting(Book::getGenre).isNull();
    }

    @Test
    @DisplayName("change должен возвращать объект с изменёнными параметрами")
    void change() {
        bookService.create(TITLE);
        var changedBook = bookService.change(NEW_TITLE);

        assertThat(changedBook).isNotNull()
                .extracting(Book::getTitle)
                .isEqualTo(NEW_TITLE);
    }

    @Test
    @DisplayName("change должен складывать в кэш изменённый объект")
    void changedBookStoredInCache() {
        bookService.create(TITLE);
        var changedBook = bookService.change(NEW_TITLE);
        var cachedBook = bookService.getCache();

        assertThat(cachedBook).isNotNull()
                .isEqualToComparingFieldByField(changedBook);
    }

    @Test
    @DisplayName("change при пустом кэше должен выбросить исключение")
    void changeWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> bookService.change(TITLE));
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("wireAuthor должен класть в кэшированную книгу кэшированного автора")
    void wireAuthor() {
        var book = bookService.create(TITLE);
        var author = new Author();
        cacheHolder.setAuthor(author);
        bookService.wireAuthor();

        assertThat(book).extracting(Book::getAuthor)
                .isNotNull()
                .isSameAs(author);
    }

    @Test
    @DisplayName("wireAuthor без книги в кэше должен бросить исключение")
    void wireAuthorWithoutCachedBook() {
        var author = new Author();
        cacheHolder.setAuthor(author);

        Throwable thrown = catchThrowable(() -> bookService.wireAuthor());
        assertThat(thrown)
                .isInstanceOf(EmptyCacheException.class)
                .hasMessage("book");
    }

    @Test
    @DisplayName("wireGenre должен класть в кэшированную книгу кэшированный жанр")
    void wireGenre() {
        var book = bookService.create(TITLE);
        var genre = new Genre();
        cacheHolder.setGenre(genre);
        bookService.wireGenre();

        assertThat(book).extracting(Book::getGenre)
                .isNotNull()
                .isSameAs(genre);
    }

    @Test
    @DisplayName("wireGenre без жанра в кэше должен бросить исключение")
    void wireGenreWithoutCachedGenre() {
        bookService.create(TITLE);

        Throwable thrown = catchThrowable(() -> bookService.wireGenre());
        assertThat(thrown)
                .isInstanceOf(EmptyCacheException.class)
                .hasMessage("genre");
    }
}
