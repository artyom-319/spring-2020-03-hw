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
     * Получить результаты в виде текста
     * @return Строка с результатами
     */
    String getTextResults();
}
