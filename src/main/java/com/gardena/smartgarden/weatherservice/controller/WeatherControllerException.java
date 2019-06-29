package com.gardena.smartgarden.weatherservice.controller;

public class WeatherControllerException extends RuntimeException {
    public WeatherControllerException(String message) {
        super(message);
    }
}