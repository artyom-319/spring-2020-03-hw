package com.etn319;

import com.etn319.quiz.Quiz;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        Quiz quiz = context.getBean(Quiz.class);
        quiz.run();
        System.out.println(quiz.getTextResults());
    }
}
