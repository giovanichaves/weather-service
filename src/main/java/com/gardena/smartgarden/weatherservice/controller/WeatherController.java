package com.gardena.smartgarden.weatherservice.controller;

import com.gardena.smartgarden.weatherservice.service.Coords;
import com.gardena.smartgarden.weatherservice.service.WeatherService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity getWeather(@RequestParam("lat") final Double latitude, @RequestParam("long") final Double longitude) {
        Coords coords = new Coords(latitude, longitude);
        return ResponseEntity.ok()
                             .header("Content-Type", "application/json; charset=UTF-8")
                             .body(weatherService.retrieveCurrentConditions(coords));
    }
}