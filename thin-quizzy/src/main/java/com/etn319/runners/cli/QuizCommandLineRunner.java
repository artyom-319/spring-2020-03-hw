package com.etn319.runners.cli;

import com.etn319.quiz.Quiz;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "spring.shell.interactive.enabled", havingValue = "false")
public class QuizCommandLineRunner implements CommandLineRunner {
    private final Quiz quiz;

    public QuizCommandLineRunner(Quiz quiz) {
        this.quiz = quiz;
    }

    @Override
    public void run(String... args) {
        quiz.run();
    }
}
