package com.etn319.quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения данных о вопросах
 */
public class Question {

    /**
     * Текст вопроса
     */
    private String text;

    /**
     * Правильный ответ
     */
    private String expectedAnswer;

    /**
     * Варианты ответа на вопрос
     */
    private List<String> answerOptions = new ArrayList<>();

    public Question(String text, String expectedAnswer) {
        this.text = text;
        this.expectedAnswer = expectedAnswer;
    }

    public String getText() {
        return text;
    }

    public String getExpectedAnswer() {
        return expectedAnswer;
    }

    public List<String> getAnswerOptions() {
        return answerOptions;
    }

    public boolean hasAnswerOptions() {
        return (answerOptions != null && !answerOptions.isEmpty());
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setExpectedAnswer(String expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    public void setAnswerOptions(List<String> answerOptions) {
        this.answerOptions = answerOptions;
    }
}
