package com.etn319.shell;

import com.etn319.model.Book;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Book Commands")
@RequiredArgsConstructor
public class BookCommandHandler {
    private final BookService bookService;

    @ShellMethod(value = "Count book objects", key = "bcount")
    public String count() {
        return "Books found: " + bookService.count();
    }

    @ShellMethod(value = "Get a book by id and load it to cache", key = "bget")
    public String get(String id) {
        Optional<Book> book = bookService.getById(id);
        return book.map(Book::toString)
                .orElse("No books found");
    }

    @ShellMethod(value = "Get the first book and load it to cache", key = "bfirst")
    public String first() {
        Optional<Book> book = bookService.first();
        return book.map(Book::toString)
                .orElse("No books found");
    }

    @ShellMethod(value = "Get all books", key = "ball")
    public String getAll() {
        List<Book> books = bookService.getAll();
        if (books.isEmpty())
            return "Empty list";
        return stringifyList(books);
    }

    @ShellMethod(value = "Save cached book to DB", key = "bsave")
    public String save() {
        try {
            var book = bookService.save();
            return "Saved: " + book.toString();
        } catch (EmptyCacheException e) {
            return "Nothing to save: cache is empty";
        } catch (ServiceLayerException e) {
            return "Failed to update";
        }
    }

    @ShellMethod(value = "Delete book by id", key = "bdelete")
    public String delete(String id) {
        try {
            bookService.deleteById(id);
            return "Deleted";
        } catch (ServiceLayerException e) {
            return "Failed to delete";
        }
    }

    @ShellMethod(value = "Clear cached book", key = {"bclear", "bcl"})
    public String clearCache() {
        bookService.clearCache();
        return "Cache cleared";
    }

    @ShellMethod(value = "Get cached book", key = {"bcurrent", "bcache", "bc"})
    public String getCurrent() {
        try {
            return bookService.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = "Find books by genre using cached genre object", key = {"bgenre", "bbygenre", "bg"})
    public String getByGenre() {
        try {
            List<Book> books = bookService.getByCachedGenre();
            return stringifyList(books);
        } catch (EmptyCacheException e) {
            return "There is no cached genre. Use 'gget' command first";
        } catch (ServiceLayerException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "Find books by genre title", key = {"bgenretitle", "bgt"})
    public String getByGenreTitle(@ShellOption("-title") String title) {
        try {
            List<Book> books = bookService.getByGenreTitle(title);
            return stringifyList(books);
        } catch (ServiceLayerException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "Find books by author using cached author object", key = {"bauthor", "bbyauthor", "ba"})
    public String getByAuthor() {
        try {
            List<Book> books = bookService.getByCachedAuthor();
            return stringifyList(books);
        } catch (EmptyCacheException e) {
            return "There is no cached author. Use 'aget' command first";
        } catch (ServiceLayerException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "Find books by author id", key = {"bauthorid", "bbyauthorid", "bai"})
    public String getByAuthorId(@ShellOption("-id") String authorId) {
        try {
            List<Book> books = bookService.getByAuthorId(authorId);
            return stringifyList(books);
        } catch (ServiceLayerException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "Create a book object to store it in program cache", key = "bnew")
    public String create(@ShellOption({"title", "-t"}) String title) {
        var book = bookService.create(title);
        return String.format("Created: %s\nTo save it in database use 'bsave' command", book.toString());
    }

    @ShellMethod(value = "Update cached book object", key = "bset")
    public String change(@ShellOption({"title", "-t"}) String title) {
        try {
            var book = bookService.change(title);
            return String.format("Changed: %s\nTo save it in database use 'bsave' command", book.toString());
        } catch (EmptyCacheException e) {
            return "Nothing to change: cache is empty";
        }
    }

    @ShellMethod(value = "Wires cached author to cached book", key = {"bsetauthor", "bseta"})
    public String wireAuthor() {
        try {
            var book = bookService.wireAuthor();
            return "Author wired: " + book.toString();
        } catch (EmptyCacheException e) {
            String missed = e.getMissedEntity();
            return String.format("There is no cached %s. Create or load it first", missed);
        }
    }

    @ShellMethod(value = "Wires cached genre to cached book", key = {"bsetgenre", "bsetg"})
    public String wireGenre() {
        try {
            var book = bookService.wireGenre();
            return "Genre wired: " + book.toString();
        } catch (EmptyCacheException e) {
            String missed = e.getMissedEntity();
            return String.format("There is no cached %s. Create or load it first", missed);
        }
    }

    private static String stringifyList(List<?> list) {
        if (list.isEmpty())
            return "Empty list";
        return list.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
