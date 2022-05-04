package com.terator.service;

import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.accuracyImprover.AccuracyImprover;
import com.terator.service.generatorCreator.GeneratorCreator;
import com.terator.service.osmImporter.OsmImporter;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;

import java.util.stream.IntStream;

public class TeratorExecutor {
    OsmImporter osmImporter;
    GeneratorCreator generatorCreator;
    TrajectoryListCreator trajectoryListCreator;
    SimulationExecutor simulationExecutor;
    AccuracyChecker accuracyChecker;
    AccuracyImprover accuracyImprover;

    void execute() {
        String osmData = "";

        var city = osmImporter.importData(osmData);

        var probabilities = generatorCreator.generateProbabilities(city);

        var trajectories = trajectoryListCreator.createTrajectories(probabilities);

        var simulationResult = simulationExecutor.executeSimulation(city, trajectories);

        var accuracy = accuracyChecker.checkAccuracy(simulationResult);

        IntStream.range(0, 5)
                .forEach(value -> accuracyImprover.improve(probabilities, accuracy));
    }
}
