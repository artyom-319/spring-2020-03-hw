package com.etn319.service.questions;

import com.etn319.domain.Question;
import com.etn319.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = "file.name=test.txt")
public class QuestionFileSourceTest {
    private static final String FILE_NAME = "test.txt";
    private static final String QUESTION_TEXT = "MockedText";
    private static final String EXPECTED_ANSWER = "MockedAnswer";

    private List<String> lines;

    @Configuration
    static class Config {
        @Bean
        QuestionSource questionSource(ResourceResolver resourceResolver, QuestionConverter converter,
                                      @Value("${file.name}") String resourceFilePath) {
            return new QuestionFileSource(resourceResolver, converter, resourceFilePath);
        }
    }

    @MockBean
    private QuestionConverter converter;
    @MockBean
    private ResourceResolver resourceResolver;
    @Autowired
    private QuestionSource source;

    @BeforeEach
    public void setUp() {
        lines = getLinesFromTestFile();
        given(converter.convertLine(anyString()))
                .willReturn(new Question(QUESTION_TEXT, EXPECTED_ANSWER));
        given(resourceResolver.getResourceAsStream(anyString()))
                .willReturn(getClass().getClassLoader().getResourceAsStream(FILE_NAME));
    }

    @Test
    public void provideQuestions() {
        List<Question> questions = source.provideQuestions();
        Assertions.assertEquals(5, questions.size(), "Должно было создаться 5 вопросов");
        questions.forEach(q -> {
            Assertions.assertEquals(QUESTION_TEXT, q.getText(), "Тексты вопроса не совпадают");
            Assertions.assertEquals(EXPECTED_ANSWER, q.getExpectedAnswer(), "Правильные ответы не совпадают");
        });

        ArgumentCaptor<String> convertLineCaptor = ArgumentCaptor.forClass(String.class);
        verify(converter, times(5)).convertLine(convertLineCaptor.capture());
        Assertions.assertLinesMatch(lines, convertLineCaptor.getAllValues(),
                "Аргументы, переданные конвертеру, не совпадают");

        ArgumentCaptor<String> fileNameCapror = ArgumentCaptor.forClass(String.class);
        verify(resourceResolver, only()).getResourceAsStream(fileNameCapror.capture());
        Assertions.assertEquals(FILE_NAME, fileNameCapror.getValue(),
                "Аргумент, переданный resourceResolver'у, не совпадает с ожидаемым");
    }

    private List<String> getLinesFromTestFile() {
        List<String> result = new ArrayList<>();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Could not read from test file", e);
        }
    }
}
