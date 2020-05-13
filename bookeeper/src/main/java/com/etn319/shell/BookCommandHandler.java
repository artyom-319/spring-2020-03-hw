package com.etn319.shell;

import com.etn319.dao.ConnectedEntityDoesNotExistException;
import com.etn319.model.Book;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.EmptyConnectedEntityException;
import com.etn319.service.UpdateException;
import com.etn319.service.book.BookService;
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
    private final BookService bookService;

    @Override
    public String count() {
        return "Books found: " + bookService.count();
    }

    @Override
    public String get(long id) {
        var book = bookService.getById(id);
        if (book == null)
            return String.format("No books with id=%d were found", id);
        return book.toString();
    }

    @Override
    public String getAll() {
        List<Book> books = bookService.getAll();
        if (books.isEmpty())
            return "Empty list";
        return books.stream().map(Book::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String save() {
        try {
            var book = bookService.save();
            return "Saved: " + book.toString();
        } catch (EmptyConnectedEntityException emptyEntityException) {
            return "One or more connected entities missing in cache. Use corresponding 'get' command(s)";
        } catch (UpdateException updateException) {
            return "Failed to update";
        } catch (ConnectedEntityDoesNotExistException connectedEntityException) {
            return "One or more connected entities were not found in data source. Save them or refresh and try again";
        } catch (EmptyCacheException emptyCacheException) {
            return "Nothing to save: cache is empty";
        }
    }

    @Override
    public String delete(long id) {
        boolean isDeleted = bookService.deleteById(id);
        if (isDeleted)
            return "Deleted";
        else
            return "Failed to delete";
    }

    @Override
    public String clearCache() {
        bookService.clearCache();
        return "Cache cleared";
    }

    @Override
    public String getCurrent() {
        try {
            return bookService.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = "Find books by genre using cached genre object", key = {"get-by-genre", "by-genre", "bg"})
    public String getByGenre() {
        try {
            List<Book> books = bookService.getByCachedGenre();
            return stringifyList(books);
        } catch (EmptyCacheException e) {
            return "There is no cached genre. Use /genres/ 'get' command first";
        }
    }

    @ShellMethod(value = "Find books by genre id", key = {"get-by-genre-id", "by-genre-id", "bgi"})
    public String getByGenreId(@ShellOption("-id") long genreId) {
        List<Book> books = bookService.getByGenreId(genreId);
        return stringifyList(books);
    }

    @ShellMethod(value = "Find books by author using cached author object", key = {"get-by-author", "by-author", "ba"})
    public String getByAuthor() {
        try {
            List<Book> books = bookService.getByCachedAuthor();
            return stringifyList(books);
        } catch (EmptyCacheException e) {
            return "There is no cached author. Use /authors/ 'get' command first";
        }
    }

    @ShellMethod(value = "Find books by author id", key = {"get-by-author-id", "by-author-id", "bai"})
    public String getByAuthorId(@ShellOption("-id") long authorId) {
        List<Book> books = bookService.getByAuthorId(authorId);
        return stringifyList(books);
    }

    @ShellMethod(value = "Create a book object to store it in program cache", key = "create-book")
    public String create(@ShellOption({"--title", "-t"}) String title) {
        var book = bookService.create(title);
        return String.format("Created: %s\nTo save it in database use /books/ 'save' command", book.toString());
    }

    @ShellMethod(value = "Update cached book object", key = "change-book")
    public String change(@ShellOption({"--title", "-t"}) String title) {
        try {
            var book = bookService.change(title);
            return String.format("Changed: %s\nTo save it in database use /books/ 'save' command", book.toString());
        } catch (EmptyCacheException e) {
            return "Nothing to change: cache is empty";
        }
    }

    @ShellMethod(value = "Wires cached author to cached book", key = "set-author")
    public String wireAuthor() {
        try {
            var book = bookService.wireAuthor();
            return "Author wired: " + book.toString();
        } catch (EmptyCacheException e) {
            String missed = e.getMissedEntity();
            return String.format("There is no cached %1$s. Use 'create-%1$s' or /%1$ss/ 'get' command first", missed);
        }
    }

    @ShellMethod(value = "Wires cached genre to cached book", key = "set-genre")
    public String wireGenre() {
        try {
            var book = bookService.wireGenre();
            return "Genre wired: " + book.toString();
        } catch (EmptyCacheException e) {
            String missed = e.getMissedEntity();
            return String.format("There is no cached %1$s. Use 'create-%1$s' or /%1$ss/ 'get' command first", missed);
        }
    }

    private static String stringifyList(List<?> list) {
        if (list.isEmpty())
            return "Empty list";
        return list.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
