package com.etn319.config;

import com.etn319.health.ClearWeatherIndicator;
import com.etn319.health.ClearWeatherProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClearWeatherProperties.class)
public class ClearWeatherConfiguration {
    @Bean
    @ConditionalOnProperty("weather-service.app-id")
    public ClearWeatherIndicator clearWeatherIndicator(ClearWeatherProperties properties) {
        return new ClearWeatherIndicator(properties);
    }
}
