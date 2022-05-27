package com.terator.model.simulation;

import java.time.LocalTime;
import java.util.Map;

public record DensityInTime(
        Map<LocalTime, Long> density
) {
    public static Integer MINUTES_INTERVAL_SIMULATOR = 5;
}
