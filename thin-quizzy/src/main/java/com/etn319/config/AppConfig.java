package com.etn319.config;

import com.etn319.quiz.ConsoleQuiz;
import com.etn319.quiz.Quiz;
import com.etn319.service.QuestionConverter;
import com.etn319.service.QuestionCsvConverter;
import com.etn319.service.QuestionFileSource;
import com.etn319.service.QuestionSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${csv.delimiter}")
    private String csvDelimiter;

    @Value("${question.source.filename}")
    private String questionsFileName;

    @Bean
    public QuestionConverter questionConverter() {
        return new QuestionCsvConverter(csvDelimiter);
    }

    @Bean
    public QuestionSource questionSource(QuestionConverter converter) {
        return new QuestionFileSource(converter, questionsFileName);
    }

    @Bean
    public Quiz quiz(QuestionSource questionSource) {
        return new ConsoleQuiz(questionSource);
    }
}
