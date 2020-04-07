package com.etn319.service;

import com.etn319.quiz.Question;

/**
 * Класс конвертера строки в вопрос
 */
public interface QuestionConverter {
    Question convertLine(String line);
}
