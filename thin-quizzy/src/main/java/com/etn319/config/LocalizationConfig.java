package com.etn319.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.LocalizedResourceHelper;

import java.util.Locale;

@Configuration
public class LocalizationConfig {
    @Value("${locale.country}")
    private String country;

    @Value("${locale.language}")
    private String language;

    @Bean
    public Locale locale() {
        if (country == null || language == null)
            return Locale.getDefault();
        return new Locale(language, country);
    }

    @Bean
    public LocalizedResourceHelper localizedResourceHelper() {
        return new LocalizedResourceHelper();
    }
}
