package com.gardena.smartgarden.weatherservice.client.darksky;

public class DarkSkyApiException extends RuntimeException {
    public DarkSkyApiException(String message) {
        super(message);
    }
}
