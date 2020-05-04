package com.etn319.service.questions;

import com.etn319.domain.Question;

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
