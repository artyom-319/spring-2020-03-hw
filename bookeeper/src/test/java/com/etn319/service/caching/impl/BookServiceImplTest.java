package com.etn319.service.caching.impl;

import com.etn319.dao.mongo.BookMongoRepository;
import com.etn319.dao.mongo.CommentMongoRepository;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.model.Genre;
import com.etn319.service.EntityDoesNotExistException;
import com.etn319.service.caching.CacheHolder;
import com.etn319.service.caching.EmptyCacheException;
import com.etn319.service.caching.api.BookCachingService;
import com.etn319.service.common.api.BookService;
import com.etn319.service.common.impl.BookServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@SpringBootTest
class BookServiceImplTest {
    private static final long COUNT = -100;
    private static final String NOT_EXISTING_ID = "NOT_EXISTING_ID";
    private static final String EXISTING_ID = "EXISTING_ID";
    private static final String TITLE = "20 Years Later";
    private static final String NEW_TITLE = "10 Years Later";
    private static final Book BOOK = new Book("1L", TITLE, new Author(), new Genre());

    private final List<Book> allBooks = Collections.nCopies(5, BOOK);
    private final List<Book> booksByGenre = Collections.nCopies(4, BOOK);
    private final List<Book> booksByAuthor = Collections.nCopies(3, BOOK);
    private final List<Comment> commentsByBook =
            Collections.nCopies(2, new Comment("text", "commenter", BOOK));

    @Configuration
    static class Config {
        @Bean
        public BookService bookServiceBase(BookMongoRepository dao, CommentMongoRepository commentDao) {
            return new BookServiceImpl(dao, commentDao);
        }

        @Bean
        public CacheHolder cacheHolder() {
            return new CacheHolder();
        }

        @Bean
        public BookCachingService bookService(BookService baseService, CacheHolder cacheHolder) {
            return new BookCachingServiceImpl(baseService, cacheHolder);
        }
    }

    @MockBean
    private BookMongoRepository bookDao;
    @MockBean
    private CommentMongoRepository commentDao;
    @Autowired
    private CacheHolder cacheHolder;
    @Autowired
    private BookCachingService bookService;

    @BeforeEach
    public void setUp() {
        given(bookDao.count()).willReturn(COUNT);
        given(bookDao.findById(EXISTING_ID)).willReturn(Optional.of(BOOK));
        given(bookDao.findById(NOT_EXISTING_ID)).willReturn(Optional.empty());
        given(bookDao.existsById(EXISTING_ID)).willReturn(true);
        given(bookDao.existsById(NOT_EXISTING_ID)).willReturn(false);
        given(bookDao.findAll()).willReturn(allBooks);
        given(commentDao.findAllByBook(BOOK)).willReturn(commentsByBook);
        doNothing().when(bookDao).deleteById(anyString());

        given(bookDao.findAllByAuthor_id(anyString())).willReturn(booksByAuthor);
        given(bookDao.findAllByGenreTitle(anyString())).willReturn(booksByGenre);
        given(bookDao.save(any())).will(inv -> {
            Book book = inv.getArgument(0);
            book.setId(EXISTING_ID);
            return book;
        });

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
    @DisplayName("findById должен вызывать метод dao.findById, возвращать его результат")
    void getByIdDelegatesCallToDao() {
        var id = EXISTING_ID;
        Optional<Book> book = bookService.getById(id);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(bookDao, only()).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);

        assertThat(book).isPresent();
        assertThat(book.orElseThrow()).isEqualToComparingFieldByField(BOOK);
    }

    @Test
    @DisplayName("findById должен кэшировать результат")
    void getByIdStoresResultInCache() {
        Optional<Book> book = bookService.getById(EXISTING_ID);

        assertThat(book).isPresent();
        assertThat(bookService)
                .extracting(BookCachingService::getCache)
                .isNotNull()
                .isEqualTo(book.orElseThrow());
    }

    @Test
    @DisplayName("findAll должен вызывать dao.findAll и возвращать результат")
    void getAll() {
        List<Book> actual = bookService.getAll();

        verify(bookDao, only()).findAll();
        assertThat(actual).isSameAs(allBooks);
    }

    @Test
    @DisplayName("findAll не должен сохранять объекты в кэше")
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

        verify(bookDao).save(argumentCaptor.capture());
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
        var idToDelete = EXISTING_ID;
        bookService.deleteById(idToDelete);
        var argumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(bookDao).deleteById(argumentCaptor.capture());
        verify(bookDao).deleteById(argumentCaptor.getValue());
        assertThat(argumentCaptor.getValue()).isEqualTo(idToDelete);
    }

    @Test
    @DisplayName("deleteById по несуществующему id должен вызывать dao.deleteById с тем же аргументом и кидать исключение")
    void deleteByIncorrectId() {
        Throwable thrown = catchThrowable(() -> bookService.deleteById(NOT_EXISTING_ID));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(bookDao, only()).existsById(argumentCaptor.capture());
        assertThat(thrown).isInstanceOf(EntityDoesNotExistException.class);
    }

    @Test
    void getByGenreTitle() {
        List<Book> booksByGenre = bookService.getByGenreTitle("genreTitle");
        var argumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(bookDao, only()).findAllByGenreTitle(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo("genreTitle");
        assertThat(booksByGenre).isSameAs(booksByGenre);
    }

    @Test
    void getByCachedAuthor() {
        var author = new Author("id0", "name", "country");
        cacheHolder.setAuthor(author);
        List<Book> booksByAuthor = bookService.getByCachedAuthor();
        var argumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(bookDao, only()).findAllByAuthor_id(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(author.getId());
        assertThat(booksByAuthor).isSameAs(booksByAuthor);
    }


    @Test
    @DisplayName("create должен создавать объект книги с переданными параметрами")
    void create() {
        var book = bookService.create(TITLE);

        assertThat(book).isNotNull()
                .extracting(Book::getId, Book::getTitle)
                .containsExactly(null, TITLE);
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
