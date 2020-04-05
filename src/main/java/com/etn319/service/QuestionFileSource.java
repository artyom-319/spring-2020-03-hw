package com.etn319.service;

import com.etn319.quiz.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuestionFileSource implements QuestionSource {
    private String resourceFilePath;

    private QuestionConverter converter;

    public QuestionFileSource(QuestionConverter converter, String resourceFilePath) {
        this.converter = converter;
        this.resourceFilePath = resourceFilePath;
    }

    @Override
    public List<Question> provideQuestions() {
        List<Question> questions = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceFilePath)) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                Question question = converter.convertLine(line);
                questions.add(question);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return questions;
    }
}
