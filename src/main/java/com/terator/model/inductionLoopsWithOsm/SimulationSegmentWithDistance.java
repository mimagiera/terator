package com.terator.model.inductionLoopsWithOsm;

import com.terator.model.simulation.SimulationSegment;

public record SimulationSegmentWithDistance(
        SimulationSegment simulationSegment,
        double distance
) {
}
