package com.gardena.smartgarden.weatherservice.controller;

import com.gardena.smartgarden.weatherservice.api.Condition;
import com.gardena.smartgarden.weatherservice.client.common.WeatherApiClient;
import com.gardena.smartgarden.weatherservice.client.darksky.DarkSkyApiClient;
import com.gardena.smartgarden.weatherservice.client.openweather.OpenWeatherApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@RestController
public class WeatherController {

    private final List<WeatherApiClient> weatherApiClients;

    public WeatherController(OpenWeatherApiClient openWeatherClient, DarkSkyApiClient darkSkyClient) {
        this.weatherApiClients = new LinkedList<>();
        this.weatherApiClients.add(openWeatherClient);
        this.weatherApiClients.add(darkSkyClient);
    }

    @GetMapping("/weather")
    public Condition getWeather(@RequestParam("lat") final Double latitude, @RequestParam("long") final Double longitude) throws Exception {
        OptionalDouble avgTemp = this.weatherApiClients.parallelStream()
                .map(c -> c.getCondition(latitude, longitude))
                .filter(c -> c.isPresent())
                .map(Optional::get)
                .mapToDouble(Condition::getTemperature)
                .average();

        return new Condition(
                avgTemp.orElseThrow(() -> new WeatherControllerException("All weather suppliers failed"))
        );
    }
}