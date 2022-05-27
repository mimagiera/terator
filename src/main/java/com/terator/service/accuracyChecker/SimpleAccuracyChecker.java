package com.terator.service.accuracyChecker;

import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.SimulationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleAccuracyChecker implements AccuracyChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccuracyChecker.class);

    @Override
    public GeneratedTrajectoriesAccuracy checkAccuracy(SimulationResult simulationResult
    ) {
        LOGGER.info("Starting checking accuracy");

        return new GeneratedTrajectoriesAccuracy();
    }
}
