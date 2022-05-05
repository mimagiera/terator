package com.terator.service.accuracyChecker;

import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.SimulationResult;
import org.springframework.stereotype.Service;

@Service
public class SimpleAccuracyChecker implements AccuracyChecker {
    @Override
    public GeneratedTrajectoriesAccuracy checkAccuracy(SimulationResult simulationResult
    ) {
        return new GeneratedTrajectoriesAccuracy();
    }
}
