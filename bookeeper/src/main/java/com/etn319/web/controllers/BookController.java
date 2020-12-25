package com.etn319.web.controllers;

import com.etn319.model.Book;
import com.etn319.service.common.api.BookService;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private final BookService service;

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
        //todo: тут пока будет пустой список приходить, надо заполнять в дао
        model.addAttribute("comments", book.getComments());
        return "book_details";
    }

    @GetMapping("/books/edit")
    public String editBookView(Model model, @PathVariable("id") String bookId) {
        log.info("GET /books/edit?id={} received", bookId);
        Book book = service.getById(bookId).orElseThrow(NotFoundException::new);
        model.addAttribute("book", book);
        return "book_details";
    }

    @PostMapping("/books/edit")
    public String editBook(Model model, BookDto bookDto) {
        log.info("POST /books/edit?id={} received", bookDto.getId());
        Book savedBook = service.save(bookDto.toDao());
        model.addAttribute("book", BookDto.ofDao(savedBook));
        return "book_details";
    }

    @DeleteMapping("/books/{id}")
    public String deleteBook(Model model, @PathVariable("id") String bookId) {
        log.info("DELETE /books/{} received", bookId);
        service.deleteById(bookId);
        return bookList(model);
    }
}
