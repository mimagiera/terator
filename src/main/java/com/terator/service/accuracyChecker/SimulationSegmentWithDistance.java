package com.terator.service.accuracyChecker;

import com.terator.model.simulation.SimulationSegment;

public record SimulationSegmentWithDistance(
        SimulationSegment simulationSegment,
        double distance
) {
}
