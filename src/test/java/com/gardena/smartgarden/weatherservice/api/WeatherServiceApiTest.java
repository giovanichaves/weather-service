package com.gardena.smartgarden.weatherservice.api;

import com.gardena.smartgarden.weatherservice.client.Conditions;
import com.gardena.smartgarden.weatherservice.client.darksky.DarkSkyApiClient;
import com.gardena.smartgarden.weatherservice.client.openweather.OpenWeatherApiClient;
import com.gardena.smartgarden.weatherservice.service.Coords;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(MockitoExtension.class)
public class WeatherServiceApiTest {

    @MockBean
    private OpenWeatherApiClient openWeatherApiClient;
    @MockBean
    private DarkSkyApiClient darkSkyApiClient;

    @Test
    @DisplayName("Returns Condition API for requested coordinates above 0")
    void returnsConditionAPIAboveZero() {
        Coords coords = new Coords(1.0, 2.0);
        when(openWeatherApiClient.getWeatherConditions(coords)).thenReturn(new Conditions(13.0));
        when(darkSkyApiClient.getWeatherConditions(coords)).thenReturn(new Conditions(17.0));

        given()
                .queryParam("lat", coords.getLatitude())
                .queryParam("long", coords.getLongitude())
        .get("/weather")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .body("temperature", equalTo(15.0f))
                .body("frost_warning", equalTo(false));
    }

    @Test
    @DisplayName("Returns Condition API for requested coordinates below 0")
    void returnsConditionAPIBelowZero() {
        Coords coords = new Coords(2.0, 3.0);
        when(openWeatherApiClient.getWeatherConditions(coords)).thenReturn(new Conditions(-13.0));
        when(darkSkyApiClient.getWeatherConditions(coords)).thenReturn(new Conditions(-17.0));

        given()
                .queryParam("lat", coords.getLatitude())
                .queryParam("long", coords.getLongitude())
        .get("/weather")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .body("temperature", equalTo(-15.0f))
                .body("frost_warning", equalTo(true));
    }
}
