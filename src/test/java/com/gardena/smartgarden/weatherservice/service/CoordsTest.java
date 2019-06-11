package com.gardena.smartgarden.weatherservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoordsTest {

    @Test
    @DisplayName("Throws exception if latitude is not between -90 and 90")
    void throwErrorWhenLatitudeIsOverLimits() {
        assertThrows(IllegalArgumentException.class,
                     () -> new Coords(-90.01, 10.0));
        assertThrows(IllegalArgumentException.class,
                     () -> new Coords(90.01, 10.0));
    }


    @Test
    @DisplayName("Throws exception if longitude is not between -180 and 180")
    void throwErrorWhenLongitudeIsOverLimits() {
        assertThrows(IllegalArgumentException.class,
                     () -> new Coords(10.0, -180.01));
        assertThrows(IllegalArgumentException.class,
                     () -> new Coords(10.0, 180.01));
    }

    @Test
    @DisplayName("Returns correct latitude and longitude")
    void returnsCorrectLatAndLong() {
        Coords coords = new Coords(15.0, 28.000008);

        assertEquals(coords.toString(), "15.0,28.000008");
    }

}