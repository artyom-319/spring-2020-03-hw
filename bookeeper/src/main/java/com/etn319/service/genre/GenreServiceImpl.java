package com.etn319.service.genre;

import com.etn319.dao.EntityNotFoundException;
import com.etn319.dao.genre.GenreDao;
import com.etn319.model.Genre;
import com.etn319.service.CacheHolder;
import com.etn319.service.UpdateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreDao dao;
    private final CacheHolder cache;
    private boolean created;

    @Override
    public int count() {
        return dao.count();
    }

    @Override
    public Genre getById(long id) {
        try {
            var genre = dao.getById(id);
            cache.setGenre(genre);
            return genre;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<Genre> getAll() {
        return dao.getAll();
    }

    @Override
    public Genre save() {
        var genre = cache.getGenre();

        if (created) {
            // todo: а если не заинсертится
            Genre inserted = dao.insert(genre);
            created = false;
            clearCache();
            return inserted;
        } else {
            try {
                Genre updated = dao.update(genre);
                clearCache();
                return updated;
            } catch (EntityNotFoundException e) {
                throw new UpdateException(e);
            }
        }
    }

    @Override
    public boolean deleteById(long id) {
        try {
            dao.deleteById(id);
            return true;
        } catch (RuntimeException e) {
            log.debug(e.toString());
            return false;
        }
    }

    @Override
    public Genre create(String title) {
        var genre = new Genre();
        genre.setTitle(title);
        created = true;
        cache.setGenre(genre);
        return genre;
    }

    @Override
    public Genre change(String title) {
        var genre = cache.getGenre();
        genre.setTitle(title);
        return genre;
    }

    private void clearCache() {
        created = false;
        cache.clearGenre();
    }
}
