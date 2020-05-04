package com.etn319.quiz;

import com.etn319.service.QuestionSource;
import com.etn319.service.io.IOService;
import com.etn319.service.message.MessageService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConsoleQuiz implements Quiz {
    private final QuestionSource questionSource;
    private final MessageService messageService;
    private final IOService ioService;
    private List<Question> questions;
    private Set<Answer> answers = new HashSet<>();
    private String userName;

    public ConsoleQuiz(QuestionSource source, MessageService messageService, IOService ioService) {
        this.questionSource = source;
        this.messageService = messageService;
        this.ioService = ioService;
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
        ioService.print(messageService.getMessage("quiz.enter.name"));
        userName = ioService.read();
        for (Question question : getQuestions()) {
            Answer answer = new Answer(question);
            while (!answer.isAccepted()) {
                try {
                    ioService.print('\n' + question.getText());
                    List<String> answerOptions = question.getAnswerOptions();
                    if (question.hasAnswerOptions()) {
                        ioService.print(messageService.getMessage("question.suggest.options"));
                        for (int i = 0; i < answerOptions.size(); i++) {
                            ioService.print(i + 1 + ". " + answerOptions.get(i));
                        }
                    }
                    answer.accept(ioService.read());
                    if (answer.isCorrect())
                        ioService.print(messageService.getMessage("answer.correct"));
                    else
                        ioService.print(messageService.getMessage("answer.incorrect",
                                question.getExpectedAnswer()));
                    answers.add(answer);
                } catch (AnswerException e) {
                    String messageCode = getMessageCode(e.getCode());
                    if (messageCode != null)
                        ioService.print(messageService.getMessage(messageCode, e.getArgs()));
                }
            }
        }
        printTextResult();
    }

    private String getMessageCode(int errorCode) {
        switch (errorCode) {
            case (Answer.ALREADY_ACCEPTED_CODE):
                return "answer.accepted.already";
            case (Answer.NO_OPTIONS_CODE):
                return "answer.options.unavailable";
            case (Answer.OPTION_OUT_OF_RANGE_CODE):
                return "answer.option.out.of.range";
            case (Answer.BAD_OPTION_CODE):
                return "answer.option.bad";
        }
        return null;
    }

    private void printTextResult() {
        long correct = answers.stream()
                .filter(Answer::isCorrect)
                .count();
        String textResult = messageService.getMessage("quiz.results",
                userName, correct, answers.size());
        ioService.print(textResult);
    }
}
