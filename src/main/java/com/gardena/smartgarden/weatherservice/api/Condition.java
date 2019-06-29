package com.gardena.smartgarden.weatherservice.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Condition {

    /**
     * Ambient temperature in °C
     */
    @JsonProperty
    private Double temperature;

    public Double getTemperature() {
        return temperature;
    }

    /**
     * Computed property to indicates whether temp is below 0 °C
     */
    @JsonProperty("frost_warning")
    public Boolean getFrostWarning() {
        return temperature < 0;
    }

    public Condition(Double temperature) {
        this.temperature = temperature;
    }
}
