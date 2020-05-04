package com.etn319.config;

import com.etn319.resource.LocalizedResourceResolver;
import com.etn319.resource.ResourceResolver;
import com.etn319.service.QuestionConverter;
import com.etn319.service.QuestionCsvConverter;
import com.etn319.service.QuestionFileSource;
import com.etn319.service.QuestionSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.LocalizedResourceHelper;

import java.util.Locale;

@Configuration
public class AppConfig {

    @Value("${csv.delimiter}")
    private String csvDelimiter;

    @Value("${question.source.filename}")
    private String questionsFileName;

    @Value("${question.source.extension}")
    private String extension;

    @Bean
    public QuestionConverter questionConverter() {
        return new QuestionCsvConverter(csvDelimiter);
    }

    @Bean
    public QuestionSource questionSource(ResourceResolver resourceResolver, QuestionConverter converter) {
        return new QuestionFileSource(resourceResolver, converter, questionsFileName);
    }

    @Bean
    public LocalizedResourceResolver resourceResolver(LocalizedResourceHelper resourceHelper, Locale locale) {
        LocalizedResourceResolver resourceResolver = new LocalizedResourceResolver(resourceHelper);
        resourceResolver.setLocale(locale);
        resourceResolver.setExtension(extension);
        return resourceResolver;
    }
}
