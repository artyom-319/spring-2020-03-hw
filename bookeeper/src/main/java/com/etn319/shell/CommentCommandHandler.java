package com.etn319.shell;

import com.etn319.model.Comment;
import com.etn319.service.EmptyCacheException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Special Comment Commands")
@RequiredArgsConstructor
public class CommentCommandHandler implements CommandHandler {
    private final CommentService service;

    @Override
    public String count() {
        return "Comments found: " + service.count();
    }

    @Override
    public String get(long id) {
        Optional<Comment> comment = service.getById(id);
        if (comment.isEmpty())
            return "No comments found";
        return comment.toString();
    }

    @Override
    public String getAll() {
        List<Comment> comments = service.getAll();
        if (comments.isEmpty())
            return "Empty list";
        return comments.stream()
                .map(Comment::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String save() {
        try {
            var comment = service.save();
            return "Saved: " + comment.toString();
        } catch (EmptyCacheException e) {
            return "Nothing to save: cache is empty";
        } catch (ServiceLayerException e) {
            return "Failed to save";
        }
    }

    @Override
    public String delete(long id) {
        try {
            service.deleteById(id);
            return "Deleted";
        } catch (ServiceLayerException e) {
            return "Failed to delete";
        }
    }

    @Override
    public String clearCache() {
        service.clearCache();
        return "Cache cleared";
    }

    @Override
    public String getCurrent() {
        try {
            return service.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = "Find comments for a book using cached book object", key = {"comments-for-book", "cfb"})
    public String getForBook() {
        try {
            List<Comment> comments = service.getByBook();
            return stringifyList(comments);
        } catch (EmptyCacheException e) {
            return "There is no cached author. Use /authors/ 'get' command first";
        }
    }

    @ShellMethod(value = "Find comments by commenter name", key = {"comments-by-commenter", "cbc"})
    public String getByCommenterName(@ShellOption({"--commenter", "-c"}) String commenter) {
        List<Comment> comments = service.getByCommenterName(commenter);
        return stringifyList(comments);
    }

    @ShellMethod(value = "Create a comment object to store it in program cache", key = "create-comment")
    public String create(@ShellOption({"--text", "-t"}) String text, @ShellOption({"--commenter", "-c"}) String commenter) {
        var comment = service.create(text, commenter);
        return String.format("Created: %s\nTo save it in database use /comments/ 'save' command", comment.toString());
    }

    @ShellMethod(value = "Update cached comment object", key = "change-comment")
    public String change(@ShellOption({"--text", "-t"}) String text, @ShellOption({"--commenter", "-c"}) String commenter) {
        try {
            var comment = service.change(text, commenter);
            return String.format("Changed: %s\nTo save it in database use /comments/ 'save' command", comment.toString());
        } catch (EmptyCacheException e) {
            return "Nothing to change: cache is empty";
        }
    }

    private static String stringifyList(List<?> list) {
        if (list.isEmpty())
            return "Empty list";
        return list.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
