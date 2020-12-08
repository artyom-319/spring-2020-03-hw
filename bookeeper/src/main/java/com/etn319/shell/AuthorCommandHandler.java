package com.etn319.shell;

import com.etn319.model.Author;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Author Commands")
@RequiredArgsConstructor
public class AuthorCommandHandler {
    private final AuthorService authorService;

    @ShellMethod(value = Commands.COUNT_TITLE, key = "acount")
    public String count() {
        return "Authors count: " + authorService.count();
    }

    @ShellMethod(value = Commands.GET_TITLE, key = "aget")
    public String get(String id) {
        Optional<Author> author = authorService.getById(id);
        return author.map(Author::toString)
                .orElse("No authors found");
    }

    @ShellMethod(value = Commands.GET_ALL_TITLE, key = "aall")
    public String getAll() {
        List<Author> authors = authorService.getAll();
        if (authors.isEmpty())
            return "Empty list";
        return authors.stream()
                .map(Author::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = Commands.SAVE_TITLE, key = "asave")
    public String save() {
        try {
            var author = authorService.save();
            return "Saved: " + author.toString();
        } catch (EmptyCacheException e) {
            return "Nothing to save: cache is empty";
        } catch (ServiceLayerException e) {
            return "Failed to save";
        }
    }

    @ShellMethod(value = Commands.DELETE_TITLE, key = "adelete")
    public String delete(String id) {
        try {
            authorService.deleteById(id);
            return "Deleted";
        } catch (ServiceLayerException e) {
            return "Failed to delete";
        }
    }

    @ShellMethod(value = Commands.CLEAR_TITLE, key = {"aclear", "acl"})
    public String clearCache() {
        authorService.clearCache();
        return "Cache cleared";
    }

    @ShellMethod(value = Commands.CACHE_TITLE, key = {"acurrent", "acache", "ac"})
    public String getCurrent() {
        try {
            return authorService.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = Commands.CREATE_TITLE, key = "anew")
    public String create(@ShellOption({"name", "-n"}) String name, @ShellOption({"country", "-c"}) String country) {
        var author = authorService.create(name, country);
        return String.format("Created: %s\nTo save it in database use 'asave' command", author.toString());
    }

    @ShellMethod(value = Commands.CHANGE_TITLE, key = "aset")
    public String change(@ShellOption({"name", "-n"}) String name, @ShellOption({"country", "-c"}) String country) {
        try {
            var author = authorService.change(name, country);
            return String.format("Changed: %s\nTo save it in database use 'asave' command", author.toString());
        } catch (EmptyCacheException e) {
            return "Nothing to change: cache is empty";
        }
    }

    private static class Commands {
        private static final String COUNT_TITLE = "Count author objects";
        private static final String GET_TITLE = "Get an author object by id and load it to cache";
        private static final String GET_ALL_TITLE = "Get all author objects";
        private static final String SAVE_TITLE = "Save cached author object to DB";
        private static final String DELETE_TITLE = "Delete an author object by id";
        private static final String CLEAR_TITLE = "Clear cached author";
        private static final String CACHE_TITLE = "Get cached author";
        private static final String CREATE_TITLE = "Create an author and store it in program cache";
        private static final String CHANGE_TITLE = "Update cached author object";
    }
}
