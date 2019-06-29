package com.gardena.smartgarden.weatherservice.client.common;

public class Conditions {
    private Double tempC;

    public Conditions(Double tempC) {
        this.tempC = tempC;
    }

    public Double getTempC() {
        return tempC;
    }
}
