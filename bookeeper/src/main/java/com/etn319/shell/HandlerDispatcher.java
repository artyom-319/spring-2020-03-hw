package com.etn319.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("Interfaces")
@RequiredArgsConstructor
public class HandlerDispatcher {
    private final AuthorCommandHandler authorCommandHandler;
    private final GenreCommandHandler genreCommandHandler;
    private final BookCommandHandler bookCommandHandler;
    private final CommentCommandHandler commentCommandHandler;
    private final DelegatingCommandHandler activeCommandHandler;

    @ShellMethod(value = "Switch to /authors/ data access interface", key = {"author", "authors"})
    public String enableAuthors() {
        activeCommandHandler.setDelegate(authorCommandHandler);
        return "Switched to /authors/";
    }

    @ShellMethod(value = "Switch to /genres/ data access interface", key = {"genre", "genres"})
    public String enableGenres() {
        activeCommandHandler.setDelegate(genreCommandHandler);
        return "Switched to /genres/";
    }

    @ShellMethod(value = "Switch to /books/ data access interface", key = {"book", "books"})
    public String enableBooks() {
        activeCommandHandler.setDelegate(bookCommandHandler);
        return "Switched to /books/";
    }

    @ShellMethod(value = "Switch to /comments/ data access interface", key = {"comment", "comments"})
    public String enableComments() {
        activeCommandHandler.setDelegate(commentCommandHandler);
        return "Switched to /comments/";
    }
}
