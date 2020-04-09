package com.etn319.quiz;

import com.etn319.service.QuestionSource;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public class ConsoleQuiz implements Quiz {
    private final QuestionSource questionSource;
    private final MessageSource messageSource;
    private final Locale locale;
    private List<Question> questions;
    private Set<Answer> answers = new HashSet<>();
    private String userName;

    public ConsoleQuiz(QuestionSource source, MessageSource messageSource, Locale locale) {
        this.questionSource = source;
        this.messageSource = messageSource;
        this.locale = locale;
    }

    private List<Question> getQuestions() {
        if (questions == null)
            questions = questionSource.provideQuestions();
        Collections.shuffle(questions);
        questions.stream()
                .filter(Question::hasAnswerOptions)
                .map(Question::getAnswerOptions)
                .forEach(Collections::shuffle);
        return questions;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(messageSource.getMessage("quiz.enter.name", null, locale));
            userName = scanTillNotEmpty(scanner);
            for (Question question : getQuestions()) {
                Answer answer = new Answer(question);
                while (!answer.isAccepted()) {
                    try {
                        System.out.println('\n' + question.getText());
                        List<String> answerOptions = question.getAnswerOptions();
                        if (question.hasAnswerOptions()) {
                            System.out.println(
                                    messageSource.getMessage("question.suggest.options", null, locale));
                            for (int i = 0; i < answerOptions.size(); i++) {
                                System.out.println(i + 1 + ". " + answerOptions.get(i));
                            }
                        }
                        answer.accept(scanTillNotEmpty(scanner));
                        if (answer.isCorrect())
                            System.out.println(messageSource.getMessage("answer.correct", null, locale));
                        else
                            System.out.println(messageSource.getMessage("answer.incorrect",
                                    new String[]{question.getExpectedAnswer()}, locale));
                        answers.add(answer);
                    } catch (AnswerException e) {
                        String messageCode = null;
                        switch (e.getCode()) {
                            case (Answer.ALREADY_ACCEPTED_CODE):
                                messageCode = "answer.accepted.already";
                                break;
                            case (Answer.NO_OPTIONS_CODE):
                                messageCode = "answer.options.unavailable";
                                break;
                            case (Answer.OPTION_OUT_OF_RANGE_CODE):
                                messageCode = "answer.option.out.of.range";
                                break;
                            case (Answer.BAD_OPTION_CODE):
                                messageCode = "answer.option.bad";
                                break;
                        }
                        if (messageCode != null)
                            System.out.println(messageSource.getMessage(messageCode, e.getArgs(), locale));
                    }
                }
            }
            printTextResult();
        }
    }

    private void printTextResult() {
        long correct = answers.stream()
                .filter(Answer::isCorrect)
                .count();
        String textResult = messageSource.getMessage("quiz.results",
                new Object[]{userName, correct, answers.size()}, locale);
        System.out.println(textResult);
    }

    private String scanTillNotEmpty(Scanner scanner) {
        String result = scanner.nextLine();

        while (result == null || result.isEmpty())
            result = scanner.nextLine();

        return result;
    }
}
