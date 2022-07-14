package com.terator.model.simulation;

import java.time.LocalTime;
import java.util.Map;

public record NumberOfCarsInTime(
        Map<LocalTime, Long> numberOfCars
) {
    public static Integer SECONDS_INTERVAL_SIMULATOR = 10;
}
