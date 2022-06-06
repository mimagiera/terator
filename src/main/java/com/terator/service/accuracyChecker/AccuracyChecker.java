package com.terator.service.accuracyChecker;

import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.SimulationResult;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.model.inductionLoops.DetectorLocation;
import com.terator.model.simulation.SimulationSegment;

import java.util.Map;
import java.util.Set;

public interface AccuracyChecker {
    GeneratedTrajectoriesAccuracy checkAccuracy(
            SimulationResult simulationResult,
            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments,
            Map<DetectorLocation, SimulationSegment> detectorLocationToSimulationSegment
    );
}
