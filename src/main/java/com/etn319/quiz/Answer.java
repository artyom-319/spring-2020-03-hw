package com.etn319.quiz;

import java.util.List;
import java.util.Objects;

/**
 * Класс для хранения данных об ответе на вопрос
 */
public class Answer {
    private static final String VAR_PATTERN = "/";
    private Question question;
    private String text;
    private boolean isCorrect;

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
            throw new RuntimeException("Ответ уже был принят");
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
                throw new RuntimeException("Question without options - usage of \'/\' pattern not supported\n" +
                        "Type exact answer");

            String sOption = parsed.substring(VAR_PATTERN.length()).trim();
            int optionsSize = answerOptions.size();
            try {
                int optionNumber = Integer.parseInt(sOption);
                if (optionNumber <= 0 || optionNumber > optionsSize)
                    throw new RuntimeException("Option must be in 1..." + optionsSize + ". Found: " + optionNumber);

                parsed = answerOptions.get(optionNumber - 1);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Must be a number: " + sOption);
            }
        }
        return parsed;
    }
}
