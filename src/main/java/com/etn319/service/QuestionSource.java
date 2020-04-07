package com.etn319.service;

import com.etn319.quiz.Question;

import java.util.List;

/**
 * Сервис, который предоставляет вопросы
 */
public interface QuestionSource {
    /**
     * Получить вопросы
     * @return список вопросов
     */
    List<Question> provideQuestions();
}
