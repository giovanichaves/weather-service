package com.gardena.smartgarden.weatherservice.client.darksky;

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
public class DarkSkyApiClient implements WeatherProvider {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DarkSkyApiClient.class);

    private final DarkSkyProperties properties;
    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://api.darksky.net";

    @Override
    public Conditions getWeatherConditions(Coords coords) {
        String url = requestUrl(coords);

        return parseConditionResponse(fetchResponse(url));
    }

    private JsonNode fetchResponse(String url) {
        LOGGER.debug("Fetching data darksky");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        LOGGER.debug("Received DarkSky response: '{}'", response);

        Metrics.counter("weatherService_darkSkyApi_statusCode", "statusCode", String.valueOf(response.getStatusCode().value())).increment();

        if (response.getStatusCode() != HttpStatus.OK) {
            LOGGER.error("Could not get response: {}", url);
            throw new DarkSkyApiException("Could not get response");
        }

        try {
            return objectMapper.readTree(response.getBody());
        } catch (IOException e) {
            Metrics.counter("weatherService_darkSkyApi_error_parsingPayload", "type", "IOException").increment();
            LOGGER.error("Invalid response: {}", url);
            throw new DarkSkyApiException("Invalid response");
        }
    }

    private Conditions parseConditionResponse(JsonNode response) {
        final JsonNode errorNode = response.at("/error");
        if (!errorNode.isMissingNode()) {
            Metrics.counter("weatherService_darkSkyApi_error_parsingPayload", "type", "errorNode").increment();
            LOGGER.error("Received error response. Description: '{}'", errorNode.asText());
            throw new DarkSkyApiException(errorNode.asText());
        }

        JsonNode temperatureNode = response.at("/currently/temperature");
        if (temperatureNode.isMissingNode()) {
            Metrics.counter("weatherService_darkSkyApi_error_parsingPayload", "type", "temperatureNode").increment();
            LOGGER.error("Received response without temperature.");
            throw new DarkSkyApiException("Received response without temperature.");
        }

        return new Conditions(temperatureNode.doubleValue());
    }

    private String requestUrl(Coords coords) {
        return String.format("%s/forecast/%s/%s?units=si&lang=en", BASE_URL, properties.getApiKey(), coords.toString());
    }
}
