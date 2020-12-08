package com.etn319.shell;

import com.etn319.model.Genre;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.api.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Genre Commands")
@RequiredArgsConstructor
public class GenreCommandHandler {
    private final GenreService genreService;

    @ShellMethod(value = "Count genre objects", key = "count")
    public String count() {
        return "Genres found: " + genreService.count();
    }

    @ShellMethod(value = "Get a genre object by title and load it to cache", key = "gget")
    public String get(String title) {
        Optional<Genre> genre = genreService.getByTitle(title);
        return genre.map(Genre::toString)
                .orElse("No genres found");
    }

    @ShellMethod(value = "Get all genres", key = "gall")
    public String getAll() {
        List<Genre> genres = genreService.getAll();
        if (genres.isEmpty())
            return "Empty list";
        return genres.stream()
                .map(Genre::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = "Clear cached genre", key = {"gclear", "gcl"})
    public String clearCache() {
        genreService.clearCache();
        return "Cache cleared";
    }

    @ShellMethod(value = "Get cached genre", key = {"gcurrent", "gcache", "gc"})
    public String getCurrent() {
        try {
            return genreService.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = "Create a genre object and store it in program cache", key = "gnew")
    public String create(@ShellOption({"title", "-t"}) String title) {
        var genre = genreService.create(title);
        return String.format("Created: %s\nTo save it in database use /genres/ 'save' command", genre.toString());
    }

    @ShellMethod(value = "Update cached genre object", key = "gset")
    public String change(@ShellOption({"title", "-t"}) String title) {
        try {
            var genre = genreService.change(title);
            return String.format("Changed: %s\nTo save it in database use /genres/ 'save' command", genre.toString());
        } catch (EmptyCacheException e) {
            return "Nothing to change: cache is empty";
        }
    }
}
