package com.etn319.shell;

import com.etn319.model.Comment;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.caching.EmptyCacheException;
import com.etn319.service.caching.api.CommentCachingService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Comment Commands")
@RequiredArgsConstructor
public class CommentCommandHandler {
    private final CommentCachingService service;

    @ShellMethod(value = "Count comments", key = "ccount")
    public String count() {
        return "Comments found: " + service.count();
    }

    @ShellMethod(value = "Get comment by id and load it to cache", key = "cget")
    public String get(String id) {
        Optional<Comment> comment = service.getById(id);
        return comment.map(Comment::toString)
                .orElse("No comments found");
    }

    @ShellMethod(value = "Get the first comment and load it to cache", key = "cfirst")
    public String first() {
        Optional<Comment> comment = service.first();
        return comment.map(Comment::toString)
                .orElse("No comments found");
    }

    @ShellMethod(value = "Get all comments", key = "call")
    public String getAll() {
        List<Comment> comments = service.getAll();
        return stringifyList(comments);
    }

    @ShellMethod(value = "Save cached comment to DB", key = "csave")
    public String save() {
        try {
            var comment = service.save();
            return "Saved: " + comment.toString();
        } catch (EmptyCacheException e) {
            return "Nothing to save: cache is empty";
        } catch (ServiceLayerException e) {
            return "Failed to save: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Delete comment by id", key = "cdelete")
    public String delete(String id) {
        try {
            service.deleteById(id);
            return "Deleted";
        } catch (ServiceLayerException e) {
            return "Failed to delete";
        }
    }

    @ShellMethod(value = "Clear cached comment", key = {"cclear", "ccl"})
    public String clearCache() {
        service.clearCache();
        return "Cache cleared";
    }

    @ShellMethod(value = "Get cached comment", key = {"ccurrent", "ccache", "cc"})
    public String getCurrent() {
        try {
            return service.getCache().toString();
        } catch (EmptyCacheException e) {
            return "Empty cache";
        }
    }

    @ShellMethod(value = "Find comments for a book using cached book object", key = {"cbook", "cforbook", "cfb"})
    public String getForBook() {
        try {
            List<Comment> comments = service.getByBook();
            return stringifyList(comments);
        } catch (EmptyCacheException e) {
            return "There is no cached book. Use 'bget' command first";
        }
    }

    @ShellMethod(value = "Find comments by user name", key = {"cuser", "cbyuser"})
    public String getByCommenterName(@ShellOption({"user", "-u"}) String commenter) {
        List<Comment> comments = service.getByCommenterName(commenter);
        return stringifyList(comments);
    }

    @ShellMethod(value = "Wire comment to cached book", key = {"csetbook", "csetb", "csb"})
    public String wireBook() {
        try {
            var comment = service.wireBook();
            return "Comment wired to book: " + comment.toString();
        } catch (EmptyCacheException e) {
            String missed = e.getMissedEntity();
            return String.format("There is no cached %s. Create or load it first", missed);
        }
    }

    @ShellMethod(value = "Create a comment object and store it in program cache", key = "cnew")
    public String create(
            @ShellOption({"text", "-t"}) String text,
            @ShellOption(value = {"commenter", "-c"}, defaultValue = ShellOption.NULL) String commenter) {
        var comment = service.create(text, commenter);
        return String.format("Created: %s\nTo save it in database use 'csave' command", comment.toString());
    }

    @ShellMethod(value = "Update cached comment object", key = "cset")
    public String change(
            @ShellOption(value = {"text", "-t"}, defaultValue = ShellOption.NULL) String text,
            @ShellOption(value = {"commenter", "-c"}, defaultValue = ShellOption.NULL) String commenter) {
        try {
            var comment = service.change(text, commenter);
            return String.format("Changed: %s\nTo save it in database use 'csave' command", comment.toString());
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
