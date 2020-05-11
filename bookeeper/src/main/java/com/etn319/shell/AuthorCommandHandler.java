package com.etn319.shell;

import com.etn319.model.Author;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.UpdateException;
import com.etn319.service.author.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Special Author Commands")
@RequiredArgsConstructor
public class AuthorCommandHandler implements CommandHandler {
    private final AuthorService authorService;

    @Override
    public String count() {
        return "Authors count: " + authorService.count();
    }

    @Override
    public String get(long id) {
        var author = authorService.getById(id);
        if (author == null)
            return "No authors found";
        return author.toString();
    }

    @Override
    public String getAll() {
        List<Author> authors = authorService.getAll();
        if (authors.isEmpty())
            return "Empty list";
        return authors.stream().map(Author::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String save() {
        try {
            var author = authorService.save();
            return "Saved: " + author.toString();
        } catch (UpdateException updateException) {
            return "Failed to save";
        } catch (EmptyCacheException cacheException) {
            return "Nothing to save: cache is empty";
        }
    }

    @Override
    public String delete(long id) {
        boolean isDeleted = authorService.deleteById(id);
        if (isDeleted)
            return "Deleted";
        else
            return "Failed to delete";
    }

    @Override
    public String clearCache() {
        authorService.clearCache();
        return "Cache cleared";
    }

    @Override
    public String getCurrent() {
        try {
            return authorService.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = "Create an author object to store it in program cache", key = "create-author")
    public String create(@ShellOption({"--name", "-n"}) String name, @ShellOption({"--country", "-s"}) String country) {
        var author = authorService.create(name, country);
        return String.format("Created: %s\nTo save it in database use /authors/ 'save' command", author.toString());
    }

    @ShellMethod(value = "Update cached author object", key = "change-author")
    public String change(@ShellOption({"--name", "-n"}) String name, @ShellOption({"--country", "-s"}) String country) {
        try {
            var author = authorService.change(name, country);
            return String.format("Changed: %s\nTo save it in database use /authors/ 'save' command", author.toString());
        } catch (EmptyCacheException e) {
            return "Nothing to change: cache is empty";
        }
    }
}
