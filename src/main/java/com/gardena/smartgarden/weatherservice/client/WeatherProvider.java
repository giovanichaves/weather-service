package com.gardena.smartgarden.weatherservice.client;

import com.gardena.smartgarden.weatherservice.service.Coords;

public interface WeatherProvider {

    Conditions getWeatherConditions(Coords coords);
}
