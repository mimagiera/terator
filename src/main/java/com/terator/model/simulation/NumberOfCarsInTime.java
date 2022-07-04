package com.terator.model.simulation;

import java.time.LocalTime;
import java.util.Map;

public record NumberOfCarsInTime(
        Map<LocalTime, Long> numberOfCars
) {
    public static Integer MINUTES_INTERVAL_SIMULATOR = 5;
}
