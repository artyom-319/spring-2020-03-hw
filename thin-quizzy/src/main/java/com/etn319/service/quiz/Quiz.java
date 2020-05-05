package com.etn319.service.quiz;

import com.etn319.domain.Question;

import java.util.List;

/**
 * Интерфейс работы с квизом
 */
public interface Quiz {
    /**
     * Получить список вопросов в квизе
     * @return список вопросов
     */
    List<Question> questions();

    /**
     * Запустить квиз
     */
    void run();

    /**
     * Регистрация участника
     * @param userName имя участника
     */
    void applyParticipant(String userName);

    /**
     * Сброс участника
     */
    void dropParticipant();

    /**
     * Проверка, заявлен ли участник на квиз
     * @return true - заявлен, false - нет
     */
    boolean participantApplied();

    /**
     * Проверка, завершён ли квиз
     * @return true - завершён, false - нет
     */
    boolean isFinished();

    /**
     * Возвращает количество верных ответов
     * @return количество верных ответов
     */
    int countCorrectAnswers();

    /**
     * Возвращает количество неверных ответов
     * @return количество неверных ответов
     */
    int countIncorrectAnswers();

    /**
     * Возвращает количество данных ответов
     * @return количество данных ответов
     */
    int countGivenAnswers();

    /**
     * Обнулить результаты
     */
    void clearResults();

    /**
     * Поделиться результатами
     */
    void shareResults();
}
