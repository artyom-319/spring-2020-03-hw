package com.etn319.service.impl;

import com.etn319.dao.mongo.GenreMongoRepositoryCustom;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.api.GenreService;
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
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName("Genre Service")
class GenreServiceImplTest {
    private static final long COUNT = -100;
    private static final String NOT_EXISTING_TITLE = "NOT_EXISTING_TITLE";
    private static final String EXISTING_TITLE = "EXISTING_TITLE";
    private static final String TITLE = "Comedy";
    private static final String NEW_TITLE = "Tragedy";
    private static final Genre GENRE = new Genre(TITLE);

    @Configuration
    static class Config {
        @Bean
        public GenreService genreService(GenreMongoRepositoryCustom genreDao) {
            return new GenreServiceImpl(genreDao, new CacheHolder());
        }
    }

    @MockBean
    private GenreMongoRepositoryCustom genreDao;
    @Autowired
    private GenreService genreService;

    @BeforeEach
    public void setUp() {
        given(genreDao.count()).willReturn(COUNT);
        given(genreDao.findByTitle(anyString())).willReturn(Optional.of(GENRE));
        given(genreDao.findByTitle(NOT_EXISTING_TITLE)).willReturn(Optional.empty());
        given(genreDao.findAll()).willReturn(Collections.emptyList());

        genreService.clearCache();
    }

    @Test
    @DisplayName("count должен вызывать метод dao.count и возвращать его результат")
    void count() {
        long cnt = genreService.count();
        verify(genreDao, only()).count();
        assertThat(cnt).isEqualTo(COUNT);
    }

    @Test
    @DisplayName("findByTitle должен вызывать метод dao.findByTitle, возвращать его результат")
    void getByTitleDelegatesCallToDao() {
        var title = "";
        Optional<Genre> genre = genreService.getByTitle(title);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(genreDao, only()).findByTitle(captor.capture());
        assertThat(captor.getValue()).isEqualTo(title);

        assertThat(genre).isPresent();
        assertThat(genre.orElseThrow()).isEqualToComparingFieldByField(GENRE);
    }

    @Test
    @DisplayName("findByTitle должен кэшировать результат")
    void getByIdStoresResultInCache() {
        Optional<Genre> genre = genreService.getByTitle(EXISTING_TITLE);

        assertThat(genre).isPresent();
        assertThat(genreService)
                .extracting(GenreService::getCache)
                .isNotNull()
                .isEqualTo(genre.orElseThrow());
    }

    @Test
    @DisplayName("findAll должен вызывать dao.findAll и возвращать результат")
    void getAll() {
        List<Genre> expected = Collections.emptyList();
        List<Genre> actual = genreService.getAll();

        verify(genreDao, only()).findAll();
        assertThat(actual).isSameAs(expected);
    }

    @Test
    @DisplayName("findAll не должен сохранять объекты в кэше")
    void getAllDoesNotStoreCache() {
        genreService.getAll();
        Throwable thrown = catchThrowable(() -> genreService.getCache());
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("create должен создавать объект жанра с переданными параметрами")
    void create() {
        var genre = genreService.create(TITLE);

        assertThat(genre).isNotNull()
                .extracting(Genre::getTitle)
                .isEqualTo(TITLE);
    }

    @Test
    @DisplayName("create должен складывать в кэш созданный объект жанра")
    void createdGenreStoredInCache() {
        var genre = genreService.create(TITLE);
        var cachedGenre = genreService.getCache();

        assertThat(cachedGenre).isNotNull()
                .isEqualToComparingFieldByField(genre);
    }

    @Test
    @DisplayName("change должен возвращать объект с изменёнными параметрами")
    void change() {
        genreService.create(TITLE);
        var changedGenre = genreService.change(NEW_TITLE);

        assertThat(changedGenre).isNotNull()
                .extracting(Genre::getTitle)
                .isEqualTo(NEW_TITLE);
    }

    @Test
    @DisplayName("change должен складывать в кэш изменённый объект")
    void changedGenreStoredInCache() {
        genreService.create(TITLE);
        var changedGenre = genreService.change(NEW_TITLE);
        var cachedGenre = genreService.getCache();

        assertThat(cachedGenre).isNotNull()
                .isEqualToComparingFieldByField(changedGenre);
    }

    @Test
    @DisplayName("change при пустом кэше должен выбросить исключение")
    void changeWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> genreService.change(TITLE));
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }
}
