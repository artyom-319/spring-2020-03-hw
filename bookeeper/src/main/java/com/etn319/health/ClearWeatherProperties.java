package com.etn319.health;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather-service")
@Data
public class ClearWeatherProperties {
    private String cityId;
    private String appId;
}
