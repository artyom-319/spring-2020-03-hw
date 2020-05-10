package com.etn319.shell;

import com.etn319.dao.genre.GenreDao;
import com.etn319.model.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Special Genre Commands")
@RequiredArgsConstructor
public class GenreCommandHandler implements CommandHandler {
    private final GenreDao dao;
    private final CacheHolder cache;
    private boolean created;

    @Override
    public String count() {
        return "Genres found: " + dao.count();
    }

    @Override
    public String get(long id) {
        var genre = dao.getById(id);
        if (genre == null)
            return "No genres found";
        cache.setGenre(genre);
        return genre.toString();
    }

    @Override
    public String getAll() {
        List<Genre> genres = dao.getAll();
        return genres.stream().map(Genre::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String save() {
        var genre = cache.getGenre();
        if (genre == null)
            return "Nothing to save: cache is empty";
        if (true) {//created) {
            // todo: а если не заинсертится
            Genre inserted = dao.insert(genre);
            created = false;
            cache.setGenre(null);
            return "Inserted: " + inserted.toString();
        } else {
            // todo: saved -> updated
            boolean isSaved = dao.update(genre);
            if (isSaved) {
                cache.setGenre(null);
                return "Updated: " + genre.toString();
            } else
                return "Failed to update";
        }
    }

    @Override
    public String delete(long id) {
        boolean isDeleted = dao.deleteById(id);
        if (isDeleted)
            return "Deleted";
        else
            return "Failed to delete";
    }

    @ShellMethod(value = "Create a genre object to store it in program cache", key = "create-genre")
    public String create(@ShellOption({"--title", "-t"}) String title) {
        var genre = new Genre();
        genre.setTitle(title);
        created = true;
        cache.setGenre(genre);
        return String.format("Created: %s\nTo save it in database use /genres/ 'save' command", genre.toString());
    }

    @ShellMethod(value = "Update cached genre object", key = "change-genre")
    public String change(@ShellOption({"--title", "-t"}) String title) {
        var genre = cache.getGenre();
        if (genre == null)
            return "Nothing to change: cache is empty";
        genre.setTitle(title);
        return String.format("Changed: %s\nTo save it in database use /genres/ 'save' command", genre.toString());
    }
}
