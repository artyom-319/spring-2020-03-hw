package com.etn319.service.caching.impl;

import com.etn319.dao.mongo.AuthorMongoRepository;
import com.etn319.model.Author;
import com.etn319.service.caching.CacheHolder;
import com.etn319.service.caching.EmptyCacheException;
import com.etn319.service.EntityDoesNotExistException;
import com.etn319.service.caching.api.AuthorCachingService;
import com.etn319.service.common.api.AuthorService;
import com.etn319.service.common.impl.AuthorServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName("Author Service")
class AuthorServiceImplTest {
    private static final long COUNT = -100;
    private static final String NOT_EXISTING_ID = "NOT_EXISTING_ID";
    private static final String EXISTING_ID = "EXISTING_ID";
    private static final String NAME = "Pyotr";
    private static final String COUNTRY = "Russia";
    private static final String NEW_NAME = "Peter";
    private static final String NEW_COUNTRY = "Germany";
    private static final Author AUTHOR = new Author("1L", NAME, COUNTRY);

    @Configuration
    static class Config {
        @Bean
        public AuthorService authorServiceBase(AuthorMongoRepository dao) {
            return new AuthorServiceImpl(dao);
        }

        @Bean
        public AuthorCachingService authorService(AuthorService baseService) {
            return new AuthorCachingServiceImpl(baseService, new CacheHolder());
        }
    }

    @MockBean
    private AuthorMongoRepository authorDao;
    @Autowired
    private AuthorCachingService authorService;

    @BeforeEach
    public void setUp() {
        given(authorDao.count()).willReturn(COUNT);
        given(authorDao.findById(EXISTING_ID)).willReturn(Optional.of(AUTHOR));
        given(authorDao.findById(NOT_EXISTING_ID)).willReturn(Optional.empty());
        given(authorDao.existsById(EXISTING_ID)).willReturn(true);
        given(authorDao.existsById(NOT_EXISTING_ID)).willReturn(false);
        given(authorDao.findAll()).willReturn(Collections.emptyList());
        doNothing().when(authorDao).deleteById(anyString());

        authorService.clearCache();
    }

    @Test
    @DisplayName("count должен вызывать метод dao.count и возвращать его результат")
    void count() {
        long cnt = authorService.count();
        verify(authorDao, only()).count();
        assertThat(cnt).isEqualTo(COUNT);
    }

    @Test
    @DisplayName("findById должен вызывать метод dao.findById, возвращать его результат")
    void getByIdDelegatesCallToDao() {
        var id = EXISTING_ID;
        Optional<Author> author = authorService.getById(id);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(authorDao, only()).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);

        assertThat(author).isPresent();
        assertThat(author.orElseThrow()).isEqualToComparingFieldByField(AUTHOR);
    }

    @Test
    @DisplayName("findById должен кэшировать результат")
    void getByIdStoresResultInCache() {
        Optional<Author> author = authorService.getById(EXISTING_ID);

        assertThat(author).isPresent();
        assertThat(authorService)
                .extracting(AuthorCachingService::getCache)
                .isNotNull()
                .isEqualTo(author.orElseThrow());
    }

    @Test
    @DisplayName("findAll должен вызывать dao.findAll и возвращать результат")
    void getAll() {
        List<Author> expected = Collections.emptyList();
        List<Author> actual = authorService.getAll();

        verify(authorDao, only()).findAll();
        assertThat(actual).isSameAs(expected);
    }

    @Test
    @DisplayName("findAll не должен сохранять объекты в кэше")
    void getAllDoesNotStoreCache() {
        authorService.getAll();
        Throwable thrown = catchThrowable(() -> authorService.getCache());
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("save должен вызывать dao.save, передавать ему значение из кэша")
    void saveNewObject() {
        var author = authorService.create(NAME, COUNTRY);
        authorService.save();
        var argumentCaptor = ArgumentCaptor.forClass(Author.class);

        verify(authorDao, only()).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isSameAs(author);
    }

    @Test
    @DisplayName("save при пустом кэше должен выбросить исключение")
    void saveWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> authorService.save());
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("deleteById должен вызывать dao.deleteById с тем же аргументом")
    void deleteById() {
        var idToDelete = EXISTING_ID;
        authorService.deleteById(idToDelete);
        var argumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(authorDao).deleteById(argumentCaptor.capture());
        verify(authorDao).deleteById(argumentCaptor.getValue());
        assertThat(argumentCaptor.getValue()).isEqualTo(idToDelete);
    }

    @Test
    @DisplayName("deleteById по несуществующему id должен вызывать dao.deleteById с тем же аргументом и кидать исключение")
    void deleteByIncorrectId() {
        Throwable thrown = catchThrowable(() -> authorService.deleteById(NOT_EXISTING_ID));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(authorDao, only()).existsById(argumentCaptor.capture());
        assertThat(thrown).isInstanceOf(EntityDoesNotExistException.class);
    }

    @Test
    @DisplayName("create должен создавать объект автора с переданными параметрами")
    void create() {
        var author = authorService.create(NAME, COUNTRY);

        assertThat(author).isNotNull()
                .extracting(Author::getId, Author::getName, Author::getCountry)
                .containsExactly(null, NAME, COUNTRY);
    }

    @Test
    @DisplayName("create должен складывать в кэш созданный объект автора")
    void createdAuthorStoredInCache() {
        var author = authorService.create(NAME, COUNTRY);
        var cachedAuthor = authorService.getCache();

        assertThat(cachedAuthor).isNotNull()
                .isEqualToComparingFieldByField(author);
    }

    @Test
    @DisplayName("change должен возвращать объект с изменёнными параметрами")
    void change() {
        authorService.create(NAME, COUNTRY);
        var changedAuthor = authorService.change(NEW_NAME, NEW_COUNTRY);

        assertThat(changedAuthor).isNotNull()
                .extracting(Author::getName, Author::getCountry)
                .containsExactly(NEW_NAME, NEW_COUNTRY);
    }

    @Test
    @DisplayName("change должен складывать в кэш изменённый объект")
    void changedAuthorStoredInCache() {
        authorService.create(NAME, COUNTRY);
        var changedAuthor = authorService.change(NEW_NAME, NEW_COUNTRY);
        var cachedAuthor = authorService.getCache();

        assertThat(cachedAuthor).isNotNull()
                .isEqualToComparingFieldByField(changedAuthor);
    }

    @Test
    @DisplayName("change должен брать объект из кэша и менять только те поля, куда передаётся не null")
    void changeOnlyOneField() {
        authorService.create(NAME, COUNTRY);
        var changedAuthor = authorService.change(NEW_NAME, null);

        assertThat(changedAuthor).isNotNull()
                .extracting(Author::getId, Author::getName, Author::getCountry)
                .containsExactly(null, NEW_NAME, COUNTRY);
    }

    @Test
    @DisplayName("change при пустом кэше должен выбросить исключение")
    void changeWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> authorService.change(NAME, COUNTRY));
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }
}
