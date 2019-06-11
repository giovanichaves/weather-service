package com.gardena.smartgarden.weatherservice.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Condition {

    /**
     * Ambient temperature in °C
     */
    @JsonProperty
    private Double temperature;

    @JsonProperty(value = "frost_warning")
    private boolean frostWarning;

}
