package com.etn319.quiz;

import java.util.List;
import java.util.Objects;

/**
 * Класс для хранения данных об ответе на вопрос
 */
public class Answer {
    private static final String VAR_PATTERN = "/";
    private final Question question;
    private String text;
    private boolean isCorrect;

    public static final int ALREADY_ACCEPTED_CODE = 0;
    public static final int NO_OPTIONS_CODE = 1;
    public static final int OPTION_OUT_OF_RANGE_CODE = 2;
    public static final int BAD_OPTION_CODE = 3;

    public Answer(Question question) {
        this.question = question;
    }

    /**
     * Принимает ответ на вопрос. Может принять как полный ответ, так и его номер при наличии вариантов ответа.
     * Проверяет правильность ответа
     * @param attempt попытка ответа на вопрос
     * @throws RuntimeException если в качестве ответа был дан нераспознанный вариант ответа или ответ был дан ранее
     */
    public void accept(String attempt) throws RuntimeException {
        if (isAccepted())
            throw new AnswerException(ALREADY_ACCEPTED_CODE);
        text = parseAnswer(attempt);
        isCorrect = text.equalsIgnoreCase(question.getExpectedAnswer());
    }

    /**
     * Проверяет, был ли ответ принят
     * @return true, если ответ принят, false, если нет
     */
    public boolean isAccepted() {
        return text != null;
    }

    /**
     * Проверяет правильность данного ответа
     * @return true, если ответ верен, false, если нет
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    private String parseAnswer(String answer) {
        Objects.requireNonNull(answer);
        List<String> answerOptions = question.getAnswerOptions();
        String parsed = answer.trim();

        if (parsed.startsWith(VAR_PATTERN)) {
            if (answerOptions.isEmpty())
                throw new AnswerException(NO_OPTIONS_CODE);

            String sOption = parsed.substring(VAR_PATTERN.length()).trim();
            int optionsSize = answerOptions.size();
            try {
                int optionNumber = Integer.parseInt(sOption);
                if (optionNumber <= 0 || optionNumber > optionsSize)
                    throw new AnswerException(OPTION_OUT_OF_RANGE_CODE, optionsSize, optionNumber);

                parsed = answerOptions.get(optionNumber - 1);
            } catch (NumberFormatException e) {
                throw new AnswerException(BAD_OPTION_CODE, sOption);
            }
        }
        return parsed;
    }
}
