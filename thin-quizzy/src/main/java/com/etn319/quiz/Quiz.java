package com.etn319.quiz;

/**
 * Интерфейс работы с квизом
 */
public interface Quiz {
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
     * Обнулить результаты
     */
    void clearResults();

    /**
     * Поделиться результатами
     */
    void shareResults();
}
