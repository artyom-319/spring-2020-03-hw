package com.etn319.web.controllers;

import com.etn319.service.common.api.BookService;
import com.etn319.service.common.api.GenreService;
import com.etn319.web.dto.BookDto;
import com.etn319.web.dto.GenreDto;
import com.etn319.web.dto.mappers.BookMapper;
import com.etn319.web.dto.mappers.GenreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class GenreController {
    private final GenreService service;
    private final BookService bookService;

    @GetMapping("/genres")
    public String list(Model model) {
        List<GenreDto> genres = service.getAll()
                .stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute("genres", genres);
        return "genres";
    }

    @GetMapping("/genres/{title}/books")
    public String booksByGenre(Model model, @PathVariable("title") String title) {
        List<BookDto> books = bookService.getByGenreTitle(title)
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute("books", books);
        return "books";
    }
}
