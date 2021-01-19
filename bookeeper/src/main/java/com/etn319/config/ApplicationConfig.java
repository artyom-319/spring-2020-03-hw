package com.etn319.config;

import com.etn319.web.CustomRequestLoggingFilter;
import com.github.cloudyrock.mongock.Mongock;
import com.github.cloudyrock.mongock.SpringMongockBuilder;
import com.mongodb.MongoClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MongoProps.class)
public class ApplicationConfig {
    private static final String CHANGELOGS_PACKAGE = "com.etn319.dao.mongo.bee";

    @Bean
    @ConditionalOnProperty(value = "spring.data.mongodb.mongock-enabled", havingValue = "true")
    public Mongock mongock(MongoProps mongoProps, MongoClient mongoClient) {
        return new SpringMongockBuilder(mongoClient, mongoProps.getDatabase(), CHANGELOGS_PACKAGE)
                .build();
    }

    @Bean
    public CustomRequestLoggingFilter customRequestLoggingFilter() {
        CustomRequestLoggingFilter filter = new CustomRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("");
        filter.setAfterMessageSuffix("");
        return filter;
    }
}
