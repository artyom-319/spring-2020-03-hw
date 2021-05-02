package com.etn319.health;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
public class ClearWeatherIndicator implements HealthIndicator {
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5";
    private static final String METRIC_UNITS = "metric";
    private static final int CODE_CLEAR = 800;
    private static final int CODE_FEW_CLOUDS = 801;
    private final WebClient webClient;
    private final ClearWeatherProperties properties;

    public ClearWeatherIndicator(ClearWeatherProperties properties) {
        this.properties = properties;
        webClient = WebClient.builder()
                .baseUrl(OPEN_WEATHER_MAP_URL)
                .filter((request, nextFilter) -> {
                    log.debug("Request URI={}", request.url().toString());
                    return nextFilter.exchange(request);
                })
                .build();
    }

    @Override
    public Health health() {
        JsonNode jsonResponse = getCurrentWeather();
        Optional<Integer> weatherCode = Optional.ofNullable(jsonResponse)
                .map(rs -> rs.at("/weather/0/id"))
                .map(JsonNode::intValue);
        if (weatherCode.isEmpty()) {
            return Health.outOfService()
                    .withDetail("Reason", "Не получили ответа от сервиса погоды")
                    .build();
        }
        boolean isClear = weatherCode.get() == CODE_CLEAR || weatherCode.get() == CODE_FEW_CLOUDS;
        String weatherSummary = getWeatherSummary(jsonResponse);
        String weatherDescription = getWeatherDescription(jsonResponse);
        if (isClear) {
            return Health.up()
                    .withDetail("Reason", "Солнце светит над датацентром, всё ок!")
                    .withDetail("Summary", weatherSummary)
                    .withDetail("Weather Description", weatherDescription)
                    .build();
        }
        return Health.down()
                .withDetail("Reason", "Датацентр работает на солнечных батареях, скоро всё сядет :(")
                .withDetail("Summary", weatherSummary)
                .withDetail("Weather Description", weatherDescription)
                .build();
    }

    private JsonNode getCurrentWeather() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("id", properties.getCityId())
                        .queryParam("units", METRIC_UNITS)
                        .queryParam("APPID", properties.getAppId())
                        .build())
                .exchange()
                .flatMap(r -> r.bodyToMono(JsonNode.class))
                .block();
    }

    private String getWeatherSummary(JsonNode response) {
        return Optional.of(response)
                .map(rs -> rs.at("/weather/0/main"))
                .map(JsonNode::textValue)
                .orElse("No summary");
    }

    private String getWeatherDescription(JsonNode response) {
        return Optional.of(response)
                .map(rs -> rs.at("/weather/0/description"))
                .map(JsonNode::textValue)
                .orElse("No description");
    }
}
