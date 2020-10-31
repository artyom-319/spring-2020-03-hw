package com.etn319.service.impl;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.api.GenreDao;
import com.etn319.dao.datajpa.GenreRepository;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.ServiceLayerException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName("Genre Service")
class GenreServiceImplTest {
    private static final long COUNT = -100;
    private static final long NOT_EXISTING_ID = 100L;
    private static final String TITLE = "Comedy";
    private static final String NEW_TITLE = "Tragedy";
    private static final Genre GENRE = new Genre(1L, TITLE);

    @Configuration
    static class Config {
        @Bean
        public GenreService genreService(GenreRepository genreDao) {
            return new GenreServiceImpl(genreDao, new CacheHolder());
        }
    }

    @MockBean
    private GenreDao genreDao;
    @Autowired
    private GenreService genreService;

    @BeforeEach
    public void setUp() {
        given(genreDao.count()).willReturn(COUNT);
        given(genreDao.findById(anyLong())).willReturn(Optional.of(GENRE));
        given(genreDao.findAll()).willReturn(Collections.emptyList());
        doNothing().when(genreDao).deleteById(longThat(l -> l != NOT_EXISTING_ID));
        doThrow(EntityNotFoundException.class).when(genreDao).deleteById(NOT_EXISTING_ID);

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
    @DisplayName("findById должен вызывать метод dao.findById, возвращать его результат")
    void getByIdDelegatesCallToDao() {
        var id = 1L;
        Optional<Genre> genre = genreService.getById(id);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(genreDao, only()).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);

        assertThat(genre).isPresent();
        assertThat(genre.orElseThrow()).isEqualToComparingFieldByField(GENRE);
    }

    @Test
    @DisplayName("findById должен кэшировать результат")
    void getByIdStoresResultInCache() {
        var id = 1L;
        Optional<Genre> genre = genreService.getById(id);

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
    @DisplayName("save должен вызывать dao.save, передавать ему значение из кэша")
    void saveNewObject() {
        var genre = genreService.create(TITLE);
        genreService.save();
        var argumentCaptor = ArgumentCaptor.forClass(Genre.class);

        verify(genreDao, only()).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isSameAs(genre);
    }

    @Test
    @DisplayName("save при пустом кэше должен выбросить исключение")
    void saveWithEmptyCache() {
        Throwable thrown = catchThrowable(() -> genreService.save());
        assertThat(thrown).isInstanceOf(EmptyCacheException.class);
    }

    @Test
    @DisplayName("deleteById должен вызывать dao.deleteById с тем же аргументом")
    void deleteById() {
        var idToDelete = 1L;
        genreService.deleteById(idToDelete);
        var argumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(genreDao, only()).deleteById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(idToDelete);
    }

    @Test
    @DisplayName("deleteById по несуществующему id должен вызывать dao.deleteById с тем же аргументом и кидать исключение")
    void deleteByIncorrectId() {
        Throwable thrown = catchThrowable(() -> genreService.deleteById(NOT_EXISTING_ID));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(genreDao, only()).deleteById(argumentCaptor.capture());
        assertThat(thrown).isInstanceOf(ServiceLayerException.class);
    }

    @Test
    @DisplayName("create должен создавать объект жанра с переданными параметрами")
    void create() {
        var genre = genreService.create(TITLE);

        assertThat(genre).isNotNull()
                .extracting(Genre::getId, Genre::getTitle)
                .containsExactly(0L, TITLE);
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
