package com.etn319.web.controllers;

import com.etn319.dao.mongo.UserMongoRepository;
import com.etn319.model.Author;
import com.etn319.model.ServiceUser;
import com.etn319.service.common.api.AuthorService;
import com.etn319.service.common.api.BookService;
import com.etn319.web.dto.AuthorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {
    private ObjectWriter jackson = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private List<Author> all = List.of(
            new Author("id1", "name1", "country1"),
            new Author("id2", "name2", "country2"),
            new Author("id3", "name3", "country3")
    );
    @MockBean
    private AuthorService service;
    @MockBean
    private BookService bookService;
    @MockBean
    private UserMongoRepository userRepository;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        given(service.getAll()).willReturn(all);
        given(service.first()).willReturn(Optional.of(all.get(0)));
        given(service.getById(ArgumentMatchers.anyString())).willReturn(Optional.empty());
        given(service.getById("id1")).willReturn(Optional.of(all.get(0)));
        given(service.getById("id2")).willReturn(Optional.of(all.get(1)));
        given(service.getById("id3")).willReturn(Optional.of(all.get(2)));
        given(service.save(any(Author.class)))
                .willAnswer(inv -> inv.getArgument(0));
        given(userRepository.findByName(anyString()))
                .willReturn(Optional.of(new ServiceUser("test_user", "pass", emptyList())));
    }

    @Test
    @WithMockUser
    void getAllAuthors_shouldReturnServiceResult() throws Exception {
        mvc.perform(get("/api/authors/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id",
                        is(List.of("id1", "id2", "id3"))))
                .andExpect(jsonPath("$[*].name",
                        is(List.of("name1", "name2", "name3"))));
    }

    @Test
    @WithMockUser
    void getAuthor_shouldReturnSingleResult() throws Exception {
        mvc.perform(get("/api/authors/id1/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", is(emptyList())))
                .andExpect(jsonPath("$.id", is("id1")))
                .andExpect(jsonPath("$.name", is("name1")));
    }

    @Test
    @WithMockUser
    void getNotExistingAuthor_shouldReturn404() throws Exception {
        mvc.perform(get("/api/authors/id0/"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void postAuthor_shouldPassDomainAuthorToService() throws Exception {
        AuthorDto dto = AuthorDto.builder()
                .name("new-author")
                .country("new-country")
                .build();
        ArgumentCaptor<Author> captor = ArgumentCaptor.forClass(Author.class);
        mvc.perform(
                post("/api/authors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(jackson.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("new-author")));
        verify(service).save(captor.capture());
        assertThat(captor.getValue())
                .extracting(Author::getName, Author::getCountry)
                .containsExactly("new-author", "new-country");
    }
}
