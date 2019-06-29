package com.gardena.smartgarden.weatherservice.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
     * Indicates whether temp is below 0 °C
     */
    @JsonProperty("frost_warning")
    private Boolean frostWarning;

    @JsonIgnore
    public Boolean getFrostWarning() {
        return frostWarning;
    }

    public Condition(Double temperature, Boolean frostWarning) {
        this.temperature = temperature;
        this.frostWarning = frostWarning;
    }
}
