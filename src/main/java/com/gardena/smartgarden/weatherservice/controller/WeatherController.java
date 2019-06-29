package com.gardena.smartgarden.weatherservice.controller;

import com.gardena.smartgarden.weatherservice.api.Condition;
import com.gardena.smartgarden.weatherservice.client.common.WeatherApiClient;
import com.gardena.smartgarden.weatherservice.client.openweather.OpenWeatherApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherApiClient weatherApiClient;

    public WeatherController(OpenWeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    @GetMapping("/weather")
    public Condition getWeather(@RequestParam("lat") final Double latitude, @RequestParam("long") final Double longitude) {
        return weatherApiClient.getCondition(latitude, longitude);
    }
}