package com.etn319.shell;

import com.etn319.model.Genre;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.genre.GenreService;
import com.etn319.service.UpdateException;
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
    private final GenreService genreService;

    @Override
    public String count() {
        return "Genres found: " + genreService.count();
    }

    @Override
    public String get(long id) {
        var genre = genreService.getById(id);
        if (genre == null)
            return String.format("No genres with id=%d were found", id);
        return genre.toString();
    }

    @Override
    public String getAll() {
        List<Genre> genres = genreService.getAll();
        if (genres.isEmpty())
            return "Empty list";
        return genres.stream().map(Genre::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String save() {
        try {
            var genre = genreService.save();
            return "Saved: " + genre.toString();
        } catch (UpdateException updateException) {
            return "Failed to save";
        } catch (EmptyCacheException cacheException) {
            return "Nothing to save: cache is empty";
        }
    }

    @Override
    public String delete(long id) {
        boolean isDeleted = genreService.deleteById(id);
        if (isDeleted)
            return "Deleted";
        else
            return "Failed to delete";
    }

    @Override
    public String clearCache() {
        genreService.clearCache();
        return "Cache cleared";
    }

    @Override
    public String getCurrent() {
        try {
            return genreService.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = "Create a genre object to store it in program cache", key = "create-genre")
    public String create(@ShellOption({"--title", "-t"}) String title) {
        var genre = genreService.create(title);
        return String.format("Created: %s\nTo save it in database use /genres/ 'save' command", genre.toString());
    }

    @ShellMethod(value = "Update cached genre object", key = "change-genre")
    public String change(@ShellOption({"--title", "-t"}) String title) {
        try {
            var genre = genreService.change(title);
            return String.format("Changed: %s\nTo save it in database use /genres/ 'save' command", genre.toString());
        } catch (EmptyCacheException e) {
            return "Nothing to change: cache is empty";
        }
    }
}
