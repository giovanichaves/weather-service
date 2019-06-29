package com.gardena.smartgarden.weatherservice.client.common;

import com.gardena.smartgarden.weatherservice.api.Condition;

public interface WeatherApiClient {
    Condition getCondition(Double latitude, Double longitude);
}
