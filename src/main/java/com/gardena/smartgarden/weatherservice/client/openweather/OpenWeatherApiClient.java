package com.gardena.smartgarden.weatherservice.client.openweather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gardena.smartgarden.weatherservice.api.Condition;
import com.gardena.smartgarden.weatherservice.client.common.Conditions;
import com.gardena.smartgarden.weatherservice.client.common.WeatherApiClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenWeatherApiClient implements WeatherApiClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OpenWeatherApiClient.class);

    private final OpenWeatherProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://api.darksky.net";

    private final ResponseHandler<JsonNode> jsonNodeResponseHandler = new ResponseHandler<JsonNode>() {
        @Override
        public JsonNode handleResponse(
                final HttpResponse response) throws IOException {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                JsonNode res = objectMapper.readTree(entity.getContent());
                assertNoError(res);
                return res;
            } else {
                return null;
            }
        }

        private void assertNoError(final JsonNode response) {
            final JsonNode errorNode = response.at("/error");
            if (!errorNode.isMissingNode()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Received error response. Description: '{}'", errorNode.asText());
                }

                throw new OpenWeatherApiException(errorNode.asText());
            }
        }
    };

    public OpenWeatherApiClient(OpenWeatherProperties properties, HttpClient httpClient, ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public Condition getCondition(Double latitude, Double longitude) {
        String url = requestUrl(String.format("%s,%s", latitude, longitude));

        Conditions conditions = parseConditionResponse(fetchResponse(url));

        return new Condition(
                conditions.getTempC()
        );
    }

    private JsonNode fetchResponse(String url) {
        HttpGet httpGet = new HttpGet(url);
        try {
            JsonNode response = httpClient.execute(httpGet, jsonNodeResponseHandler);
            LOGGER.debug("Received Dark Sky response: '{}'", response);

            return response;
        } catch (IOException e) {
            LOGGER.warn("Could not get response: {}", url, e);
            throw new OpenWeatherApiException("Could not get response.");
        }
    }

    private Conditions parseConditionResponse(final JsonNode response) {
        JsonNode observation = response.at("/currently");

        return new Conditions(observation.at("/temperature").doubleValue());
    }

    private String requestUrl(String endpoint) {
        return String.format("%s/forecast/%s/%s?units=si&lang=en", BASE_URL, properties.getApiKey(), endpoint);
    }
}
