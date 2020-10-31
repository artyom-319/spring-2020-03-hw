package com.etn319.service.impl;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.AuthorDao;
import com.etn319.dao.api.BookDao;
import com.etn319.dao.api.GenreDao;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.BookService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@SpringBootTest
class BookServiceImplTest {
    private static final long COUNT = -100;
    private static final long NOT_EXISTING_ID = 100L;
    private static final String TITLE = "20 Years Later";
    private static final String NEW_TITLE = "10 Years Later";
    private static final Book BOOK = new Book(1L, TITLE, new Author(), new Genre());

    private final List<Book> allBooks = Collections.nCopies(5, BOOK);
    private final List<Book> booksByGenre = Collections.nCopies(4, BOOK);
    private final List<Book> booksByAuthor = Collections.nCopies(3, BOOK);
    private final Author author = new Author();
    private final Genre genre = new Genre();

    @Configuration
    static class Config {
        @Bean
        public CacheHolder cacheHolder() {
            return new CacheHolder();
        }

        @Bean
        public BookService bookService(BookDao bookDao, AuthorDao authorDao, GenreDao genreDao, CacheHolder cacheHolder) {
            return new BookServiceImpl(bookDao, authorDao, genreDao, cacheHolder);
        }
    }

    @MockBean
    private BookDao bookDao;
    @MockBean
    private AuthorDao authorDao;
    @MockBean
    private GenreDao genreDao;
    @Autowired
    private CacheHolder cacheHolder;
    @Autowired
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        author.setBooks(booksByAuthor);
        genre.setBooks(booksByGenre);

        given(bookDao.count()).willReturn(COUNT);
        given(bookDao.getById(anyLong())).willReturn(Optional.of(BOOK));
        given(bookDao.getAll()).willReturn(allBooks);
        doNothing().when(bookDao).deleteById(longThat(l -> l != NOT_EXISTING_ID));
        doThrow(EntityNotFoundException.class).when(bookDao).deleteById(NOT_EXISTING_ID);

        given(authorDao.getById(anyLong())).willReturn(Optional.of(author));
        given(genreDao.getById(anyLong())).willReturn(Optional.of(genre));

        bookService.clearCache();
    }

    @Test
    @DisplayName("count должен вызывать метод dao.count и возвращать его результат")
    void count() {
        long cnt = bookService.count();
        verify(bookDao, only()).count();
        assertThat(cnt).isEqualTo(COUNT);
    }

    @Test
    @DisplayName("getById должен вызывать метод dao.getById, возвращать его результат")
    void getByIdDelegatesCallToDao() {
        var id = 1L;
        Optional<Book> book = bookService.getById(id);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(bookDao, only()).getById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);

        assertThat(book).isPresent();
        assertThat(book.orElseThrow()).isEqualToComparingFieldByField(BOOK);
    }

    @Test
    @DisplayName("getById должен кэшировать результат")
    void getByIdStoresResultInCache() {
        var id = 1L;
        Optional<Book> book = bookService.getById(id);

        assertThat(book).isPresent();
        assertThat(bookService)
                .extracting(BookService::getCache)
                .isNotNull()
                .isEqualTo(book.orElseThrow());
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
    @DisplayName("save должен вызывать dao.save, передавать ему значение из кэша")
    void saveNewObject() {
        var book = bookService.create(TITLE);
        book.setGenre(new Genre());
        book.setAuthor(new Author());
        bookService.save();
        var argumentCaptor = ArgumentCaptor.forClass(Book.class);

        verify(bookDao, only()).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isSameAs(book);
    }

    @Test
    @DisplayName("save при пустом кэше должен выбросить исключение")
    void saveWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> bookService.save());
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("deleteById должен вызывать dao.deleteById с тем же аргументом")
    void deleteById() {
        var idToDelete = 1L;
        bookService.deleteById(idToDelete);
        var argumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(bookDao, only()).deleteById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(idToDelete);
    }

    @Test
    @DisplayName("deleteById по несуществующему id должен вызывать dao.deleteById с тем же аргументом и кидать исключение")
    void deleteByIncorrectId() {
        Throwable thrown = catchThrowable(() -> bookService.deleteById(NOT_EXISTING_ID));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(bookDao, only()).deleteById(argumentCaptor.capture());
        assertThat(thrown).isInstanceOf(ServiceLayerException.class);
    }

    @Test
    void getByGenreId() {
        List<Book> booksByGenre = bookService.getByGenreId(1L);
        var argumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(genreDao, only()).getById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(1L);
        assertThat(booksByGenre).isSameAs(booksByGenre);
    }

    @Test
    void getByCachedAuthor() {
        var author = new Author(2L, "name", "country");
        cacheHolder.setAuthor(author);
        List<Book> booksByAuthor = bookService.getByCachedAuthor();
        var argumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(authorDao, only()).getById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(author.getId());
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
