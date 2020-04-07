package com.etn319.quiz;

import com.etn319.service.QuestionSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ConsoleQuiz implements Quiz {
    private QuestionSource questionSource;
    private List<Question> questions;
    private Set<Answer> answers = new HashSet<>();
    private String userName;

    public ConsoleQuiz(QuestionSource source) {
        this.questionSource = source;
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
            System.out.println("Для начала введите ваши имя и фамилию:");
            userName = scanner.nextLine();
            for (Question question : getQuestions()) {
                Answer answer = new Answer(question);
                while (!answer.isAccepted()) {
                    try {
                        System.out.println('\n' + question.getText());
                        List<String> answerOptions = question.getAnswerOptions();
                        if (question.hasAnswerOptions()) {
                            System.out.println("Type whole answer or use '/' with option");
                            System.out.println("Example: '/1' to choose option 1");
                            for (int i = 0; i < answerOptions.size(); i++) {
                                System.out.println(i + 1 + ". " + answerOptions.get(i));
                            }
                        }
                        answer.accept(scanner.nextLine());
                        if (answer.isCorrect())
                            System.out.println("Верно!");
                        else
                            System.out.println("Неверно! Правильный ответ: " + question.getExpectedAnswer());
                        answers.add(answer);
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    public String getTextResults() {
        long correct = answers.stream()
                .filter(Answer::isCorrect)
                .count();
        return '\n' + userName + ", ваш результат: " + correct + " / " + answers.size() + "\nПоздравляем!";
    }
}
