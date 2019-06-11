package com.gardena.smartgarden.weatherservice.service;

import com.gardena.smartgarden.weatherservice.api.Condition;
import com.gardena.smartgarden.weatherservice.client.Conditions;
import com.gardena.smartgarden.weatherservice.client.WeatherProvider;

import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Metrics;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    private final List<WeatherProvider> weatherProviders;
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Cacheable("tempAtCoords")
    public Condition retrieveCurrentConditions(final Coords coords) {
        Double temperature = fetchAverageOfWeatherProviders(coords);

        if (temperature.isNaN()) {
            LOGGER.error("No Weather API was able to retrieve data");
            Metrics.counter("weatherService_error_noWeatherData").increment();
            throw new NoWeatherDataAvailableException("No Weather API was able to retrieve data");
        }

        return new Condition(
                roundDecimals(temperature, 2),
                isFrostWarning(temperature)
        );
    }

    private Double fetchAverageOfWeatherProviders(final Coords coords) {
        List<Future<Conditions>> conditionsFutures = weatherProviders.stream()
                                                                     .map(weatherProvider -> executor.submit(() -> weatherProvider.getWeatherConditions(coords)))
                                                                     .collect(Collectors.toList());

        return conditionsFutures.stream()
                                .map(conditionsFuture -> {

                                         try {
                                             return Optional.of(conditionsFuture.get().getTempC());
                                         } catch (Exception e) {
                                             return Optional.empty();
                                         }
                                     }
                                    )
                                .filter(Optional::isPresent)
                                .mapToDouble(temperature -> (Double) temperature.get())
                                .average()
                                .orElse(Double.NaN);
    }

    boolean isFrostWarning(final Double temperature) {
        return temperature < 0;
    }

    private double roundDecimals(Double temperature, int decimals) {
        return new BigDecimal(temperature).setScale(decimals, RoundingMode.HALF_UP).doubleValue();
    }

}