package com.gardena.smartgarden.weatherservice.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Condition {

    /**
     * Ambient temperature in °C
     */
    @JsonProperty
    private Double temperature;

    public Condition(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTemperature() {
        return temperature;
    }
}
