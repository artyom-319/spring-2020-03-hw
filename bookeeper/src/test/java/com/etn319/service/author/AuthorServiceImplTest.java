package com.etn319.service.author;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.author.AuthorDao;
import com.etn319.model.Author;
import com.etn319.service.CacheHolder;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.ServiceLayerException;
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
@DisplayName("Author Service")
class AuthorServiceImplTest {
    private static final long COUNT = -100;
    private static final long NOT_EXISTING_ID = 100L;
    private static final String NAME = "Pyotr";
    private static final String COUNTRY = "Russia";
    private static final String NEW_NAME = "Peter";
    private static final String NEW_COUNTRY = "Germany";
    private static final Author AUTHOR = new Author(1L, NAME, COUNTRY);

    @Configuration
    static class Config {
        @Bean
        public AuthorService authorService(AuthorDao authorDao) {
            return new AuthorServiceImpl(authorDao, new CacheHolder());
        }
    }

    @MockBean
    private AuthorDao authorDao;
    @Autowired
    AuthorService authorService;

    @BeforeEach
    public void setUp() {
        given(authorDao.count()).willReturn(COUNT);
        given(authorDao.getById(anyLong())).willReturn(Optional.of(AUTHOR));
        given(authorDao.getAll()).willReturn(Collections.emptyList());
        doNothing().when(authorDao).deleteById(longThat(l -> l != NOT_EXISTING_ID));
        doThrow(EntityNotFoundException.class).when(authorDao).deleteById(NOT_EXISTING_ID);

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
    @DisplayName("getById должен вызывать метод dao.getById, возвращать его результат")
    void getByIdDelegatesCallToDao() {
        var id = 1L;
        Optional<Author> author = authorService.getById(id);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(authorDao, only()).getById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);

        assertThat(author).isPresent();
        assertThat(author.orElseThrow()).isEqualToComparingFieldByField(AUTHOR);
    }

    @Test
    @DisplayName("getById должен кэшировать результат")
    void getByIdStoresResultInCache() {
        var id = 1L;
        Optional<Author> author = authorService.getById(id);

        assertThat(author).isPresent();
        assertThat(authorService)
                .extracting(AuthorService::getCache)
                .isNotNull()
                .isEqualTo(author.orElseThrow());
    }

    @Test
    @DisplayName("getAll должен вызывать dao.getAll и возвращать результат")
    void getAll() {
        List<Author> expected = Collections.emptyList();
        List<Author> actual = authorService.getAll();

        verify(authorDao, only()).getAll();
        assertThat(actual).isSameAs(expected);
    }

    @Test
    @DisplayName("getAll не должен сохранять объекты в кэше")
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
        var idToDelete = 1L;
        authorService.deleteById(idToDelete);
        var argumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(authorDao, only()).deleteById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(idToDelete);
    }

    @Test
    @DisplayName("deleteById по несуществующему id должен вызывать dao.deleteById с тем же аргументом и кидать исключение")
    void deleteByIncorrectId() {
        Throwable thrown = catchThrowable(() -> authorService.deleteById(NOT_EXISTING_ID));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(authorDao, only()).deleteById(argumentCaptor.capture());
        assertThat(thrown).isInstanceOf(ServiceLayerException.class);
    }

    @Test
    @DisplayName("create должен создавать объект автора с переданными параметрами")
    void create() {
        var author = authorService.create(NAME, COUNTRY);

        assertThat(author).isNotNull()
                .extracting(Author::getId, Author::getName, Author::getCountry)
                .containsExactly(0L, NAME, COUNTRY);
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
                .containsExactly(0L, NEW_NAME, COUNTRY);
    }

    @Test
    @DisplayName("change при пустом кэше должен выбросить исключение")
    void changeWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> authorService.change(NAME, COUNTRY));
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }
}
