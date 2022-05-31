package com.terator.service;

import com.terator.model.SimulationResult;
import com.terator.model.Trajectories;
import com.terator.model.simulation.SimulationState;
import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.accuracyImprover.AccuracyImprover;
import com.terator.service.generatorCreator.GeneratorCreator;
import com.terator.service.osmImporter.OsmImporter;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeratorExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeratorExecutor.class);
    public static final int MINUTES_INTERVAL_GENERATOR = 60;

    private final OsmImporter osmImporter;
    private final GeneratorCreator generatorCreator;
    private final TrajectoryListCreator trajectoryListCreator;
    private final SimulationExecutor simulationExecutor;
    private final AccuracyChecker accuracyChecker;
    private final AccuracyImprover accuracyImprover;

    public Trajectories execute(String osmFile) {

        var accuracy = accuracyChecker.checkAccuracy(new SimulationResult(new SimulationState(Map.of())));


//        long startProbabilities = System.currentTimeMillis();
//        var probabilities = generatorCreator.generateProbabilities();
//        long endProbabilities = System.currentTimeMillis();
//        printElapsedTime(startProbabilities, endProbabilities, "probabilities");
//
//        var city = osmImporter.importData(osmFile);
//        long endCity = System.currentTimeMillis();
//        printElapsedTime(endProbabilities, endCity, "city");
//
//        var trajectories = trajectoryListCreator.createTrajectories(probabilities, city);
//        long endTrajectories = System.currentTimeMillis();
//        printElapsedTime(endCity, endTrajectories, "trajectories");
//
//        var simulationResult = simulationExecutor.executeSimulation(city, trajectories);
//        long endSimulationResult = System.currentTimeMillis();
//        printElapsedTime(endTrajectories, endSimulationResult, "simulationResult");
//
//        var accuracy = accuracyChecker.checkAccuracy(simulationResult);
//        long endAccuracy = System.currentTimeMillis();
//        printElapsedTime(endSimulationResult, endAccuracy, "accuracy");
//
//        // todo how this should work
//        IntStream.range(0, 5)
//                .forEach(value -> accuracyImprover.improve(probabilities, accuracy));

        return null;
    }

    private static void printElapsedTime(long start, long end, String message) {
        float sec = (end - start) / 1000F;
        LOGGER.info("Elapsed {} seconds: {}", message, sec);
    }
}
