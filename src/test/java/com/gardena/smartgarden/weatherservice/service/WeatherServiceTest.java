package com.gardena.smartgarden.weatherservice.service;

import com.gardena.smartgarden.weatherservice.api.Condition;
import com.gardena.smartgarden.weatherservice.client.Conditions;
import com.gardena.smartgarden.weatherservice.client.WeatherProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    private WeatherService weatherService;

    @Mock
    private WeatherProvider weatherProvider1;

    @Mock
    private WeatherProvider weatherProvider2;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(Arrays.asList(weatherProvider1, weatherProvider2));
    }

    @Test
    @DisplayName("Returns true when temperature is below 0")
    void returnsTrueWhenTempBelowZero() {
        assertTrue(weatherService.isFrostWarning(-10.0));
        assertTrue(weatherService.isFrostWarning(-0.1));
    }

    @Test
    @DisplayName("Returns false when temperature is above 0")
    void returnsFalseWhenTempAboveZero() {
        assertFalse(weatherService.isFrostWarning(10.0));
        assertFalse(weatherService.isFrostWarning(0.1));
        assertFalse(weatherService.isFrostWarning(0.0));
        assertFalse(weatherService.isFrostWarning(-0.0));
    }

    @Test
    @DisplayName("Calculates average temperature of providers")
    void retrievesAverageTemperatureFromProviders() {
        Coords coords = new Coords(1.0, 2.0);
        when(weatherProvider1.getWeatherConditions(coords))
                .thenReturn(new Conditions(10.00));
        when(weatherProvider2.getWeatherConditions(coords))
                .thenReturn(new Conditions(20.00));

        assertEquals(
                weatherService.retrieveCurrentConditions(coords),
                new Condition(15.00, false)
                    );
    }

    @Test
    @DisplayName("Rounds average temperature of providers")
    void retrievesRoundedAverageTemperatureFromProviders() {
        Coords coords = new Coords(1.0, 2.0);
        when(weatherProvider1.getWeatherConditions(coords))
                .thenReturn(new Conditions(10.33));
        when(weatherProvider2.getWeatherConditions(coords))
                .thenReturn(new Conditions(20.92));

        assertEquals(
                weatherService.retrieveCurrentConditions(coords),
                new Condition(15.63, false)
                    );
    }

    @Test
    @DisplayName("Calculates average temperature when one provider fails")
    void retrievesAverageTemperatureWhenOneProviderFails() {
        Coords coords = new Coords(1.0, 2.0);
        when(weatherProvider1.getWeatherConditions(coords))
                .thenThrow(new RuntimeException("foo"));
        when(weatherProvider2.getWeatherConditions(coords))
                .thenReturn(new Conditions(20.00));

        assertEquals(
                weatherService.retrieveCurrentConditions(coords),
                new Condition(20.00, false)
                    );
    }

    @Test
    @DisplayName("Throws exception when all providers fail")
    void throwsExceptionWhenAllProvidersFail() {
        Coords coords = new Coords(1.0, 2.0);
        when(weatherProvider1.getWeatherConditions(coords))
                .thenThrow(new RuntimeException("foo"));
        when(weatherProvider2.getWeatherConditions(coords))
                .thenThrow(new RuntimeException("bar"));

        assertThrows(NoWeatherDataAvailableException.class, () -> {
            weatherService.retrieveCurrentConditions(coords);
        });
    }

}