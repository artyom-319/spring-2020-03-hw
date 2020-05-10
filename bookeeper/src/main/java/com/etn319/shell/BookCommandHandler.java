package com.etn319.shell;

import com.etn319.dao.book.BookDao;
import com.etn319.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Special Book Commands")
@RequiredArgsConstructor
public class BookCommandHandler implements CommandHandler {
    private final BookDao dao;
    private final CacheHolder cache;
    private boolean created;

    @Override
    public String count() {
        return "Books found: " + dao.count();
    }

    @Override
    public String get(long id) {
        var book = dao.getById(id);
        if (book == null)
            return String.format("No books with id=%d were found", id);
        cache.setBook(book);
        return book.toString();
    }

    @Override
    public String getAll() {
        List<Book> genres = dao.getAll();
        return genres.stream().map(Book::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String save() {
        var book = cache.getBook();
        if (book == null)
            return "Nothing to save: cache is empty";
        if (created) {
            Book inserted = dao.insert(book);
            created = false;
            cache.setBook(null);
            return "Inserted: " + inserted.toString();
        } else {
            boolean isSaved = dao.update(book);
            if (isSaved) {
                cache.setBook(null);
                return "Saved: " + book.toString();
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

    @ShellMethod(value = "Create a book object to store it in program cache", key = "create-book")
    public String create(@ShellOption({"--title", "-t"}) String title) {
        var book = new Book();
        book.setTitle(title);
        created = true;
        cache.setBook(book);
        return String.format("Created: %s\nTo save it in database use /books/ 'save' command", book.toString());
    }

    @ShellMethod(value = "Update cached book object", key = "change-book")
    public String change(@ShellOption({"--title", "-t"}) String title) {
        var book = cache.getBook();
        if (book == null)
            return "Nothing to change: cache is empty";
        book.setTitle(title);
        return String.format("Changed: %s\nTo save it in database use /books/ 'save' command", book.toString());
    }

    @ShellMethod(value = "Wires cached author to cached book", key = "set-author")
    public String setAuthor() {
        var book = cache.getBook();
        if (book == null) {
            return "There is no cached book. Use 'create-book' or /books/ 'get' command first";
        }
        var author = cache.getAuthor();
        if (author == null) {
            return "There is no cached author. Use 'create-author' or /authors/ 'get' command first";
        }
        book.setAuthor(author);
        return "Author wired: " + book.toString();
    }

    @ShellMethod(value = "Wires cached genre to cached book", key = "set-genre")
    public String setGenre() {
        var book = cache.getBook();
        if (book == null) {
            return "There is no cached book. Use 'create-book' or /books/ 'get' command first";
        }
        var genre = cache.getGenre();
        if (genre == null) {
            return "There is no cached genre. Use 'create-genre' or /genres/ 'get' command first";
        }
        book.setGenre(genre);
        return "Genre wired: " + book.toString();
    }
}
