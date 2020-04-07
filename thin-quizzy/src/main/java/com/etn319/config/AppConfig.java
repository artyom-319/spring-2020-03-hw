package com.etn319.config;

import com.etn319.quiz.ConsoleQuiz;
import com.etn319.quiz.Quiz;
import com.etn319.service.QuestionConverter;
import com.etn319.service.QuestionCsvConverter;
import com.etn319.service.QuestionFileSource;
import com.etn319.service.QuestionSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public QuestionConverter questionConverter() {
        return new QuestionCsvConverter(";");
    }

    @Bean
    public QuestionSource questionSource(QuestionConverter converter) {
        return new QuestionFileSource(converter, "questions.csv");
    }

    @Bean
    public Quiz quiz(QuestionSource questionSource) {
        return new ConsoleQuiz(questionSource);
    }
}
