package com.etn319.web.controllers;

import com.etn319.model.Book;
import com.etn319.service.common.api.BookService;
import com.etn319.web.NotFoundException;
import com.etn319.web.dto.BookDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {
    private static final String EXISTING_ID = "EXISTING_ID";
    private static final String NOT_EXISTING_ID = "NOT_EXISTING_ID";
    private static final String SAVED_ID = "SAVED_ID";
    private static final String SAVED_TITLE = "SAVED_TITLE";
    private static final Book SAVED = new Book(SAVED_ID, SAVED_TITLE, null, null);
    private ObjectWriter jackson = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @MockBean
    private BookService service;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        given(service.save(any())).willReturn(SAVED);
        given(service.exists(EXISTING_ID)).willReturn(true);
        given(service.exists(NOT_EXISTING_ID)).willReturn(false);
        doNothing().when(service).deleteById(EXISTING_ID);
        doThrow(NotFoundException.class).when(service).deleteById(NOT_EXISTING_ID);
    }

    @Test
    void putBook_ShouldPassBookToService() throws Exception {
        BookDto dto = BookDto.builder()
                .id("id")
                .title("new-title")
                .build();
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        mvc.perform(
                put("/api/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(jackson.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(SAVED_ID)))
                .andExpect(jsonPath("$.title", is(SAVED_TITLE)));
        verify(service).save(captor.capture());
        assertThat(captor.getValue())
                .extracting(Book::getId, Book::getTitle)
                .containsExactly("id", "new-title");
    }

    @Test
    void deleteBook_ShouldCallServiceDeleteMethod() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        mvc.perform(delete("/api/books/{id}/", EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(service).deleteById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(EXISTING_ID);
    }

    @Test
    void deleteNotExistingBook_ShouldReturn404() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        mvc.perform(delete("/api/books/{id}/", NOT_EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(service).deleteById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(NOT_EXISTING_ID);
    }
}
