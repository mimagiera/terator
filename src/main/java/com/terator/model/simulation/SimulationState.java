package com.terator.model.simulation;

import java.util.Map;

public record SimulationState(
        Map<SimulationSegment, NumberOfCarsInTime> state
) {
}
