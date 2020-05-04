package com.etn319.service;

import com.etn319.quiz.Question;
import com.etn319.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionFileSourceTest {
    private static final String FILE_NAME = "test.txt";
    private static final String QUESTION_TEXT = "MockedText";
    private static final String EXPECTED_ANSWER = "MockedAnswer";

    private List<String> lines;

    @Mock
    private QuestionConverter converter;
    @Mock
    private ResourceResolver resourceResolver;
    private QuestionFileSource source;

    @BeforeEach
    public void setUp() {
        lines = getLinesFromTestFile();
        source = new QuestionFileSource(resourceResolver, converter, FILE_NAME);
        when(converter.convertLine(anyString()))
                .thenReturn(new Question(QUESTION_TEXT, EXPECTED_ANSWER));
        when(resourceResolver.getResourceAsStream(anyString()))
                .thenReturn(getClass().getClassLoader().getResourceAsStream(FILE_NAME));
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
