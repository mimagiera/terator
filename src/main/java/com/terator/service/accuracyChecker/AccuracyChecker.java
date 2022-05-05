package com.terator.service.accuracyChecker;

import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.SimulationResult;

public interface AccuracyChecker {
    GeneratedTrajectoriesAccuracy checkAccuracy(SimulationResult simulationResult);
}
