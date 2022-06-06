package com.terator.service;

import com.terator.model.City;
import com.terator.model.Trajectories;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.accuracyImprover.AccuracyImprover;
import com.terator.service.generatorCreator.GeneratorCreator;
import com.terator.service.generatorCreator.building.BuildingType;
import com.terator.service.inductionLoops.AggregatedTrafficBySegmentService;
import com.terator.service.inductionLoopsWithOsm.FixturesLocationMatcher;
import com.terator.service.osmImporter.OsmImporter;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;
import lombok.RequiredArgsConstructor;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

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

    private final FixturesLocationMatcher fixturesLocationMatcher;

    private final AggregatedTrafficBySegmentService aggregatedTrafficBySegmentService;

    public Trajectories execute(String osmFile) {
        // executed once
        // generateInitialProbabilities
        long startProbabilities = System.currentTimeMillis();
        var probabilities = generatorCreator.generateProbabilities();
        long endProbabilities = System.currentTimeMillis();
        printElapsedTime(startProbabilities, endProbabilities, "probabilities");

        // parse OSM file
        var city = osmImporter.importData(osmFile);
        long endCity = System.currentTimeMillis();
        printElapsedTime(endProbabilities, endCity, "city");

        // fetch data from induction loops
        var aggregatedTrafficBySegments = getAggregatedTrafficBySegments();
        long endGettingAggregatedDataFromInductionLoops = System.currentTimeMillis();
        printElapsedTime(endCity, endGettingAggregatedDataFromInductionLoops,
                "gettingAggregatedDataFromInductionLoops");

        // find all building by types
        Map<BuildingType, List<AtlasEntity>> allBuildingsByType = getBuildingsByType(city);
        long endFindingBuildingsWithTypes = System.currentTimeMillis();
        printElapsedTime(endGettingAggregatedDataFromInductionLoops, endFindingBuildingsWithTypes,
                "findingBuildingsWithTypes");

        // executed multiple times to optimize result

        // generate trajectories based on generator
        var trajectories = trajectoryListCreator.createTrajectories(probabilities, city, allBuildingsByType);
        long endTrajectories = System.currentTimeMillis();
        printElapsedTime(endFindingBuildingsWithTypes, endTrajectories, "trajectories");

        // simulate trajectories
        var simulationResult = simulationExecutor.executeSimulation(city, trajectories);
        long endSimulationResult = System.currentTimeMillis();
        printElapsedTime(endTrajectories, endSimulationResult, "simulationResult");

        // find accuracy
        var detectorLocationToSimulationSegment =
                fixturesLocationMatcher.findAllMatchingSegmentsToDetectors(
                        simulationResult.simulationState().state().keySet()
                );
        var accuracy = accuracyChecker.checkAccuracy(
                simulationResult, aggregatedTrafficBySegments, detectorLocationToSimulationSegment
        );
        long endAccuracy = System.currentTimeMillis();
        printElapsedTime(endSimulationResult, endAccuracy, "accuracy");

        // todo how this should work
        IntStream.range(0, 5)
                .forEach(value -> accuracyImprover.improve(probabilities, accuracy));

        return trajectories;
    }

    private Map<BuildingType, List<AtlasEntity>> getBuildingsByType(City city) {
        return Arrays.stream(BuildingType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        buildingType -> buildingType.getEntitiesProvider().apply(city)
                ));
    }

    private Map<Integer, Set<AggregatedTrafficBySegment>> getAggregatedTrafficBySegments() {
        return StreamSupport.stream(aggregatedTrafficBySegmentService.getAll().spliterator(), false)
                .collect(Collectors.groupingBy(AggregatedTrafficBySegment::getSegmentId, Collectors.toSet()));
    }


    private static void printElapsedTime(long start, long end, String message) {
        float sec = (end - start) / 1000F;
        LOGGER.info("Elapsed {} seconds: {}", message, sec);
    }
}
