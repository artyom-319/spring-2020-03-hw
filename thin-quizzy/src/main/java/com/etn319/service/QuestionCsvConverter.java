package com.etn319.service;

import com.etn319.quiz.Question;

import java.util.Arrays;
import java.util.List;

public class QuestionCsvConverter implements QuestionConverter {
    private final String delim;

    public QuestionCsvConverter(String delim) {
        this.delim = delim;
    }

    @Override
    public Question convertLine(String line) {
        String[] strings = line.split(delim);
        if (strings.length < 2)
            throw new RuntimeException("Could not convert line - not enough data");

        String text = strings[0];
        String expectedAnswer = strings[1];
        Question question = new Question(text, expectedAnswer);
        if (strings.length > 2) {
            List<String> answerOptions = Arrays.asList(strings).subList(1, strings.length);
            question.setAnswerOptions(answerOptions);
        }
        return question;
    }
}
