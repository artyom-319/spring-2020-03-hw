package com.etn319.web.controllers;

import com.etn319.model.Book;
import com.etn319.service.common.api.AuthorService;
import com.etn319.service.common.api.BookService;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.AuthorDto;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.CommentDto;
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
public class BookController {
    private final BookService service;
    private final AuthorService authorService;

    @GetMapping("/books")
    public String list(Model model) {
        log.info("GET /books received");
        List<BookDto> bookList = service.getAll()
                .stream()
                .map(BookDto::ofDao)
                .collect(Collectors.toList());
        model.addAttribute("books", bookList);
        return "books";
    }

    @GetMapping("/books/{id}")
    public String details(Model model, @PathVariable("id") String bookId) {
        log.info("GET /books/{} received", bookId);
        Book book = service.getById(bookId).orElseThrow(notFoundExceptionSupplier(bookId));
        model.addAttribute("book", BookDto.ofDao(book));
        model.addAttribute("comments",
                book.getComments().stream()
                        .map(CommentDto::ofDao)
                        .collect(Collectors.toList()));
        return "book_details";
    }

    @GetMapping("/books/edit")
    public String editView(Model model, @RequestParam("id") String bookId) {
        log.info("GET /books/edit?id={} received", bookId);
        Book book = service.getById(bookId).orElseThrow(notFoundExceptionSupplier(bookId));
        List<AuthorDto> authors = authorService.getAll()
                .stream()
                .map(AuthorDto::ofDao)
                .collect(Collectors.toList());
        model.addAttribute("book", BookDto.ofDao(book));
        model.addAttribute("authors", authors);
        return "book_edit";
    }

    @PostMapping("/books/{id}")
    public String edit(BookDto bookDto) {
        log.info("POST /books/edit?id={} received", bookDto.getId());
        Book savedBook = service.save(bookDto.toDao());
        return "redirect:/books/" + savedBook.getId();
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        log.info("GET /books/new received");
        List<AuthorDto> authors = authorService.getAll()
                .stream()
                .map(AuthorDto::ofDao)
                .collect(Collectors.toList());
        model.addAttribute("authors", authors);
        return "book_new";
    }

    @PostMapping("/books")
    public String newBook(Model model, BookDto bookDto) {
        log.info("POST /books/ received");
        Book savedBook = service.save(bookDto.toDao());
        return "redirect:/books/" + savedBook.getId();
    }

    @GetMapping("/books/delete")
    public String deleteBook(@RequestParam("id") String bookId) {
        log.info("GET /books/delete?id={} received", bookId);
        service.deleteById(bookId);
        return "redirect:books";
    }

    private Supplier<NotFoundException> notFoundExceptionSupplier(String missingId) {
        return () -> new NotFoundException("No book found by id=" + missingId);
    }
}