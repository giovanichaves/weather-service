package com.gardena.smartgarden.weatherservice.service;

public class NoWeatherDataAvailableException extends RuntimeException {

    NoWeatherDataAvailableException(String message) {
        super(message);
    }
}
