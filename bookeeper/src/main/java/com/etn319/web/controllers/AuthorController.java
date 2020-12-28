package com.etn319.web.controllers;

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

import java.util.List;
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
                .orElseThrow(NotFoundException::new);
        List<BookDto> booksByAuthor = bookService.getByAuthorId(id)
                .stream()
                .map(BookDto::ofDao)
                .collect(Collectors.toList());
        model.addAttribute("author", author);
        model.addAttribute("books", booksByAuthor);
        return "author_details";
    }
}
