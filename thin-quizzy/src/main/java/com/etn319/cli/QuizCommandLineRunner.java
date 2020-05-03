package com.etn319.cli;

import com.etn319.quiz.Quiz;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
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
