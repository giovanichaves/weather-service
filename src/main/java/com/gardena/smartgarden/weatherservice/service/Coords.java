package com.gardena.smartgarden.weatherservice.service;

import io.micrometer.core.instrument.Metrics;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Coords {

    private final Double latitude;
    private final Double longitude;

    public Coords(Double latitude, Double longitude) {
        if (Math.abs(latitude) > 90) {
            Metrics.counter("weatherService_error_badCoordinates", "coord", "latitude");
            throw new IllegalCoordinatesException(String.format("Latitude [%s] must be between -90 and 90", latitude));
        }

        if (Math.abs(longitude) > 180) {
            Metrics.counter("weatherService_error_badCoordinates", "coord", "longitude");
            throw new IllegalCoordinatesException(String.format("Longitude [%s] must be between -180 and 180", longitude));
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
