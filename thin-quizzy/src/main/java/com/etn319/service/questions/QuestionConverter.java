package com.etn319.service.questions;

import com.etn319.domain.Question;

/**
 * Класс конвертера строки в вопрос
 */
public interface QuestionConverter {
    Question convertLine(String line);
}
