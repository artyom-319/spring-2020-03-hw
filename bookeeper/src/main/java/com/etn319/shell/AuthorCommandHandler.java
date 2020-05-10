package com.etn319.shell;

import com.etn319.dao.author.AuthorDao;
import com.etn319.model.Author;
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
    private final AuthorDao dao;
    private final CacheHolder cache;
    private boolean created;

    @Override
    public String count() {
        return "Authors count: " + dao.count();
    }

    @Override
    public String get(long id) {
        Author author = dao.getById(id);
        if (author == null)
            return "No authors found";
        cache.setAuthor(author);
        return author.toString();
    }

    @Override
    public String getAll() {
        List<Author> authors = dao.getAll();
        return authors.stream().map(Author::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String save() {
        var author = cache.getAuthor();
        if (author == null)
            return "Nothing to save: cache is empty";
        if (created) {
            Author inserted = dao.insert(author);
            created = false;
            cache.setAuthor(null);
            return "Inserted: " + inserted.toString();
        } else {
            boolean isSaved = dao.update(author);
            if (isSaved) {
                cache.setAuthor(null);
                return "Saved: " + author.toString();
            } else
                return "Not saved";
        }
    }

    @Override
    public String delete(long id) {
        boolean isDeleted = dao.deleteById(id);
        if (isDeleted)
            return "Deleted";
        else
            return "Not deleted";
    }

    @ShellMethod(value = "Create an author object to store it in program cache", key = "create-author")
    public String create(@ShellOption({"--name", "-n"}) String name, @ShellOption({"--country", "-s"}) String country) {
        var author = new Author();
        author.setName(name);
        author.setCountry(country);
        cache.setAuthor(author);
        created = true;
        return String.format("Created: %s\nTo save it in database use /authors/ 'save' command", author.toString());
    }

    @ShellMethod(value = "Update cached author object", key = "change-author")
    public String change(@ShellOption({"--name", "-n"}) String name, @ShellOption({"--country", "-s"}) String country) {
        var author = cache.getAuthor();
        if (author == null)
            return "Nothing to change: cache is empty";
        if (name != null)
            author.setName(name);
        if (country != null)
            author.setCountry(country);
        return String.format("Changed: %s\nTo save it in database use /authors/ 'save' command", author.toString());
    }
}
