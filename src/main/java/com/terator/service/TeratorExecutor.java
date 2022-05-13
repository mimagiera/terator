package com.terator.service;

import com.terator.model.Trajectories;
import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.accuracyImprover.AccuracyImprover;
import com.terator.service.generatorCreator.GeneratorCreator;
import com.terator.service.osmImporter.OsmImporter;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TeratorExecutor {
    public static final int MINUTES_INTERVAL_GENERATOR = 60;

    private final OsmImporter osmImporter;
    private final GeneratorCreator generatorCreator;
    private final TrajectoryListCreator trajectoryListCreator;
    private final SimulationExecutor simulationExecutor;
    private final AccuracyChecker accuracyChecker;
    private final AccuracyImprover accuracyImprover;

    public Trajectories execute(String osmFile) {
        var probabilities = generatorCreator.generateProbabilities();

        var city = osmImporter.importData(osmFile);

        var trajectories = trajectoryListCreator.createTrajectories(probabilities, city);

        var simulationResult = simulationExecutor.executeSimulation(city, trajectories);

        var accuracy = accuracyChecker.checkAccuracy(simulationResult);

        // todo how this should work
        IntStream.range(0, 5)
                .forEach(value -> accuracyImprover.improve(probabilities, accuracy));

        return trajectories;
    }
}
