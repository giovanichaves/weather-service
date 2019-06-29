package com.gardena.smartgarden.weatherservice.client.common;

import com.gardena.smartgarden.weatherservice.api.Condition;

import java.util.Optional;

public interface WeatherApiClient {
    Optional<Condition> getCondition(Double latitude, Double longitude);
}
