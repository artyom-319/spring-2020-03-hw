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
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private final BookService service;
    private final AuthorService authorService;

    @GetMapping("/books")
    public String bookList(Model model) {
        log.info("GET /books received");
        List<Book> bookList = service.getAll();
        model.addAttribute("books", bookList);
        return "books";
    }

    @GetMapping("/books/{id}")
    public String bookDetails(Model model, @PathVariable("id") String bookId) {
        log.info("GET /books/{} received", bookId);
        Book book = service.getById(bookId).orElseThrow(NotFoundException::new);
        model.addAttribute("book", BookDto.ofDao(book));
        model.addAttribute("comments",
                book.getComments().stream()
                        .map(CommentDto::ofDao)
                        .collect(Collectors.toList()));
        return "book_details";
    }

    @GetMapping("/books/edit")
    public String editBookView(Model model, @RequestParam("id") String bookId) {
        log.info("GET /books/edit?id={} received", bookId);
        Book book = service.getById(bookId).orElseThrow(NotFoundException::new);
        List<AuthorDto> authors = authorService.getAll()
                .stream()
                .map(AuthorDto::ofDao)
                .collect(Collectors.toList());
        model.addAttribute("book", BookDto.ofDao(book));
        model.addAttribute("authors", authors);
        return "book_edit";
    }

    @PostMapping("/books/{id}")
    public String editBook(Model model, BookDto bookDto) {
        log.info("POST /books/edit?id={} received", bookDto.getId());
        Book savedBook = service.save(bookDto.toDao());
        model.addAttribute("book", BookDto.ofDao(savedBook));
        return "redirect:/books/";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        log.info("POST /books/ received");
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
}
