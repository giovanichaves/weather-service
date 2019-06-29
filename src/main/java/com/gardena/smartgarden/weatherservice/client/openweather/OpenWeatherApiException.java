package com.gardena.smartgarden.weatherservice.client.openweather;

public class OpenWeatherApiException extends RuntimeException {
    public OpenWeatherApiException(String message) {
        super(message);
    }
}
