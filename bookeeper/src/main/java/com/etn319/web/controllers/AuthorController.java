package com.etn319.web.controllers;

import com.etn319.model.Author;
import com.etn319.service.common.api.AuthorService;
import com.etn319.service.common.api.BookService;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.AuthorDto;
import com.etn319.web.dto.BookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService service;
    private final BookService bookService;

    @GetMapping("/authors")
    public String list(Model model) {
        List<AuthorDto> authors = service.getAll()
                .stream()
                .map(AuthorDto::ofDao)
                .collect(Collectors.toList());
        model.addAttribute("authors", authors);
        return "authors";
    }

    @GetMapping("/authors/{id}")
    public String details(Model model, @PathVariable("id") String id) {
        AuthorDto author = service.getById(id)
                .map(AuthorDto::ofDao)
                .orElseThrow(notFoundExceptionSupplier(id));
        List<BookDto> booksByAuthor = bookService.getByAuthorId(id)
                .stream()
                .map(BookDto::ofDao)
                .collect(Collectors.toList());
        model.addAttribute("author", author);
        model.addAttribute("books", booksByAuthor);
        return "author_details";
    }

    @GetMapping("/authors/edit")
    public String editView(Model model, @RequestParam("id") String id) {
        log.info("GET /authors/edit?id={} received", id);
        AuthorDto author = service.getById(id)
                .map(AuthorDto::ofDao)
                .orElseThrow(notFoundExceptionSupplier(id));
        model.addAttribute("author", author);
        return "author_edit";
    }

    @PostMapping("/authors/{id}")
    public String edit(AuthorDto authorDto) {
        log.info("POST /authors/edit?id={} received", authorDto.getId());
        Author savedAuthor = service.save(authorDto.toDao());
        return "redirect:/authors/" + savedAuthor.getId();
    }

    @GetMapping("/authors/new")
    public String newAuthorView() {
        log.info("GET /authors/new received");
        return "author_new";
    }

    @PostMapping("/authors")
    public String newAuthor(AuthorDto authorDto) {
        log.info("POST /authors/ received");
        Author savedAuthor = service.save(authorDto.toDao());
        return "redirect:/authors/" + savedAuthor.getId();
    }

    @GetMapping("/authors/delete")
    public String delete(@RequestParam("id") String id) {
        log.info("GET /authors/delete?id={} received", id);
        service.deleteById(id);
        return "redirect:/authors";
    }

    private Supplier<NotFoundException> notFoundExceptionSupplier(String missingId) {
        return () -> new NotFoundException("No author found by id=" + missingId);
    }
}
