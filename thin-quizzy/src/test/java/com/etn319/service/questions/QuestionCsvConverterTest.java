package com.etn319.service.questions;

import com.etn319.domain.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class QuestionCsvConverterTest {
    private static final String DELIMITER = ":";
    private static final String QUESTION = "question";
    private static final String CORRECT_ANSWER = "correct_answer";
    private static final String OPTION1 = "option1";
    private static final String OPTION2 = "option2";
    private QuestionCsvConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new QuestionCsvConverter(DELIMITER);
    }

    @Test
    public void convertLineWithoutOptions() {
        String line = QUESTION + DELIMITER + CORRECT_ANSWER;
        Question question = converter.convertLine(line);

        assertEquals(QUESTION, question.getText(), "question.text");
        assertEquals(CORRECT_ANSWER, question.getExpectedAnswer(), "question.expectedAnswer");
        assertEquals(0, question.getAnswerOptions().size(), "question.answerOptions.size");
        assertFalse(question.hasAnswerOptions(), "question.hasAnswerOptions");
    }

    @Test
    public void convertLineWithOptions() {
        String line = new StringBuilder()
                .append(QUESTION).append(DELIMITER)
                .append(CORRECT_ANSWER).append(DELIMITER)
                .append(OPTION1).append(DELIMITER)
                .append(OPTION2).toString();
        Question question = converter.convertLine(line);

        assertEquals(QUESTION, question.getText(), "question.text");
        assertEquals(CORRECT_ANSWER, question.getExpectedAnswer(), "question.expectedAnswer");
        assertTrue(question.hasAnswerOptions(), "question.hasAnswerOptions");
        assertLinesMatch(Arrays.asList(CORRECT_ANSWER, OPTION1, OPTION2), question.getAnswerOptions(),
                "question.answerOptions.size");
    }

    @Test
    public void convertLineNotEnoughData() {
        assertThrows(RuntimeException.class, () -> converter.convertLine(QUESTION),
                "Could not convert line - not enough data");
    }
}
