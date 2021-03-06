package com.gardena.smartgarden.weatherservice.client.darksky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gardena.smartgarden.weatherservice.client.Conditions;
import com.gardena.smartgarden.weatherservice.service.Coords;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DarkSkyApiClientTest {

    private DarkSkyApiClient darkSkyApiClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DarkSkyProperties darkSkyProperties;

    @BeforeEach
    void setUp() {
        darkSkyApiClient = new DarkSkyApiClient(darkSkyProperties, restTemplate, new ObjectMapper());
    }

    @Test
    @DisplayName("Fetches and parses temperature")
    void fetchesAndParsesTemperature() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(
                        new ResponseEntity<>("{" +
                                             "\"foo\":\"bar\"," +
                                             "\"currently\":{\"temperature\": 20.0 }" +
                                             "}",
                                             HttpStatus.OK
                        ));

        Conditions conditions = darkSkyApiClient.getWeatherConditions(new Coords(1.0, 2.0));

        assertEquals(new Conditions(20.0), conditions);
    }

    @Test
    @DisplayName("Throws exception when it fetches but payload is not good")
    void throwsExceptionWhenPayloadIsWrong() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(
                        new ResponseEntity<>("{" +
                                             "\"foo\":\"bar\"," +
                                             "}",
                                             HttpStatus.OK
                        ));

        assertThrows(DarkSkyApiException.class, () -> {
            darkSkyApiClient.getWeatherConditions(new Coords(1.0, 2.0));
        });
    }

    @Test
    @DisplayName("Throws exception when response status code != 200")
    void throwsExceptionWhenStatusCodeNot200() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(
                        new ResponseEntity<>("",
                                             HttpStatus.BAD_REQUEST
                        ));

        assertThrows(DarkSkyApiException.class, () -> {
            darkSkyApiClient.getWeatherConditions(new Coords(1.0, 2.0));
        });
    }

}