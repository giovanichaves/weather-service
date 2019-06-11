package com.gardena.smartgarden.weatherservice.client.openweather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gardena.smartgarden.weatherservice.client.Conditions;
import com.gardena.smartgarden.weatherservice.client.WeatherProvider;
import com.gardena.smartgarden.weatherservice.service.Coords;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import io.micrometer.core.instrument.Metrics;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OpenWeatherApiClient implements WeatherProvider {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OpenWeatherApiClient.class);

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private final OpenWeatherProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Conditions getWeatherConditions(Coords coords) {
        String url = requestUrl(coords);

        return parseConditionResponse(fetchResponse(url));
    }

    private JsonNode fetchResponse(String url) {
        LOGGER.debug("Fetching data openweather");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        LOGGER.debug("Received OpenWeather response: '{}'", response);

        Metrics.counter("weatherService_openWeatherApi_statusCode", "statusCode", String.valueOf(response.getStatusCode().value())).increment();

        if (response.getStatusCode() != HttpStatus.OK) {
            LOGGER.error("Could not get response: {}", url);
            throw new OpenWeatherApiException("Could not get response");
        }

        try {
            return objectMapper.readTree(response.getBody());
        } catch (IOException e) {
            Metrics.counter("weatherService_openWeatherApi_error_parsingPayload", "type", "IOException").increment();
            LOGGER.error("Invalid response: {}", url);
            throw new OpenWeatherApiException("Invalid response");
        }
    }

    private Conditions parseConditionResponse(JsonNode response) {
        JsonNode temperatureNode = response.at("/main/temp");

        if (temperatureNode.isMissingNode()) {
            Metrics.counter("weatherService_openWeatherApi_error_parsingPayload", "type", "temperatureNode").increment();
            LOGGER.error("Received response without temperature.");
            throw new OpenWeatherApiException("Received response without temperature.");
        }

        return new Conditions(temperatureNode.doubleValue());
    }

    private String requestUrl(Coords coords) {
        return String.format("%s/weather?appid=%s&lat=%s&lon=%s&units=metric", BASE_URL, properties.getApiKey(), coords.getLatitude(), coords.getLongitude());
    }

}
