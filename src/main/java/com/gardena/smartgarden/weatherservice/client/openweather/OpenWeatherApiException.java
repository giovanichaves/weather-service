package com.gardena.smartgarden.weatherservice.client.openweather;

public class OpenWeatherApiException extends RuntimeException {

    OpenWeatherApiException(String message) {
        super(message);
    }
}
