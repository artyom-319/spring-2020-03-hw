package com.etn319.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoProps {
    private String host;
    private String port;
    private String database;
    private boolean mongockEnabled;
}
