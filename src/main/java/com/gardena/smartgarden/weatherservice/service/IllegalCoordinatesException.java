package com.gardena.smartgarden.weatherservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalCoordinatesException extends IllegalArgumentException {

    IllegalCoordinatesException(String message) {
        super(message);
    }
}
