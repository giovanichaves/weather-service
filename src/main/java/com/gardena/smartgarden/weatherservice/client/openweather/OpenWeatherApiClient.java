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
import java.util.Optional;

@Service
public class OpenWeatherApiClient implements WeatherApiClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OpenWeatherApiClient.class);

    private final OpenWeatherProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

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

        private void assertNoError(final JsonNode content) {
            final JsonNode errorCode = content.at("/cod");
            if (!errorCode.isMissingNode() && errorCode.asText().equals("200")) {
                return; // all good
            }

            final JsonNode errorNode = content.at("/message");
            if (!errorNode.isMissingNode() && !errorCode.isMissingNode()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Received error response. Code: {}, Description: '{}'", errorCode.asText(), errorNode.asText());
                }

                throw new OpenWeatherApiException(errorNode.asText());
            } else {
                throw new OpenWeatherApiException("missing error message or code");
            }
        }
    };

    public OpenWeatherApiClient(OpenWeatherProperties properties, HttpClient httpClient, ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public Optional<Condition> getCondition(Double latitude, Double longitude) {
        try {
            String url = requestUrl(latitude, longitude);
            Conditions conditions = parseConditionResponse(fetchResponse(url));

            return Optional.of(new Condition(
                    conditions.getTempC()
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private JsonNode fetchResponse(String url) {
        HttpGet httpGet = new HttpGet(url);
        try {
            JsonNode response = httpClient.execute(httpGet, jsonNodeResponseHandler);
            LOGGER.debug("Received OpenWeather response: '{}'", response);

            return response;
        } catch (IOException e) {
            LOGGER.warn("Could not get response: {}", url, e);
            throw new OpenWeatherApiException("Could not get response.");
        }
    }

    private Conditions parseConditionResponse(final JsonNode response) {
        JsonNode observation = response.at("/main");

        return new Conditions(observation.at("/temp").doubleValue());
    }

    private String requestUrl(double latitude, double longitude) {
        return String.format("%s?lat=%f&lon=%f&units=metric&apikey=%s", BASE_URL, latitude, longitude, properties.getApiKey());
    }
}
