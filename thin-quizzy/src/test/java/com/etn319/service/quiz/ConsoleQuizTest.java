package com.etn319.service.quiz;

import com.etn319.domain.Question;
import com.etn319.service.io.IOService;
import com.etn319.service.message.MessageService;
import com.etn319.service.questions.QuestionSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
class ConsoleQuizTest {
    private static final String USERNAME = "username";
    private static final String QUESTION = "question";
    private static final String EXPECTED_ANSWER = "expected answer";
    private static final String WRONG_ANSWER = "wrong answer";
    private List<Question> providedQuestions;
    private List<String> printedMessages;
    private Queue<String> messagesToRead;

    @Configuration
    static class Config {
        @Bean
        @Scope("prototype")
        Quiz quiz(QuestionSource questionSource, MessageService messageService, IOService ioService) {
            return new ConsoleQuiz(questionSource, messageService, ioService);
        }
    }

    @MockBean
    QuestionSource questionSource;
    @MockBean
    MessageService messageService;
    @MockBean
    IOService ioService;
    @Autowired
    Quiz quiz;

    @BeforeEach
    public void setUp() {
        providedQuestions = Collections.singletonList(new Question(QUESTION, EXPECTED_ANSWER));
        printedMessages = new ArrayList<>();
        messagesToRead = new ArrayDeque<>();

        given(questionSource.provideQuestions()).willReturn(providedQuestions);
        given(messageService.getMessage(anyString(), any()))
                .will(inv -> inv.getArgument(0, String.class));
        doAnswer(inv -> printedMessages.add(inv.getArgument(0, String.class)))
                .when(ioService).print(anyString());
        given(ioService.read()).will(inv -> messagesToRead.poll());
    }

    @Test
    void runNotLoggedIn() {
        messagesToRead.add(USERNAME);
        messagesToRead.add(EXPECTED_ANSWER);
        int messagesToReadSize = messagesToRead.size();
        quiz.run();
        verify(ioService, times(messagesToReadSize)).read();
        assertThat(printedMessages).contains("quiz.enter.name");
    }

    @Test
    void runLoggedIn() {
        quiz.applyParticipant(USERNAME);
        messagesToRead.add(EXPECTED_ANSWER);
        int messagesToReadSize = messagesToRead.size();
        quiz.run();
        verify(ioService, times(messagesToReadSize)).read();
        assertThat(printedMessages).doesNotContain("quiz.enter.name");
    }

    @Test
    void countGivenAnswers() {
        quiz.applyParticipant(USERNAME);
        messagesToRead.add(EXPECTED_ANSWER);
        quiz.run();
        assertThat(quiz.countGivenAnswers()).isEqualTo(1);
    }

    @Test
    void answerCorrectly() {
        quiz.applyParticipant(USERNAME);
        messagesToRead.add(EXPECTED_ANSWER);
        quiz.run();
        assertThat(quiz.countCorrectAnswers()).isEqualTo(1);
        assertThat(quiz.countIncorrectAnswers()).isEqualTo(0);
        assertThat(printedMessages).contains("answer.correct");
        assertThat(printedMessages).doesNotContain("answer.incorrect");
    }

    @Test
    void answerIncorrectly() {
        quiz.applyParticipant(USERNAME);
        messagesToRead.add(WRONG_ANSWER);
        quiz.run();
        assertThat(quiz.countCorrectAnswers()).isEqualTo(0);
        assertThat(quiz.countIncorrectAnswers()).isEqualTo(1);
        assertThat(printedMessages).doesNotContain("answer.correct");
        assertThat(printedMessages).contains("answer.incorrect");
    }

    @Test
    void applyParticipant() {
        quiz.applyParticipant(USERNAME);
        assertThat(quiz.participantApplied()).isTrue();
    }

    @Test
    void dropParticipant() {
        quiz.applyParticipant(USERNAME);
        quiz.dropParticipant();
        assertThat(quiz.participantApplied()).isFalse();
    }

    @Test
    void isFinished() {
        messagesToRead.add(USERNAME);
        for (Question ignored : providedQuestions) {
            messagesToRead.add(EXPECTED_ANSWER);
        }
        assertThat(quiz.isFinished()).isFalse();
        quiz.run();
        assertThat(quiz.isFinished()).isTrue();
    }
}
