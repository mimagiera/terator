package com.terator.service;

import com.terator.metaheuristic.ProbabilitiesToVariables;
import com.terator.model.City;
import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.SimulationResult;
import com.terator.model.generatorTable.Probabilities;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.generatorCreator.building.BuildingType;
import com.terator.service.inductionLoops.AggregatedTrafficBySegmentService;
import com.terator.service.inductionLoopsWithOsm.FixturesLocationMatcher;
import com.terator.service.osmImporter.OsmImporter;
import com.terator.service.routesCreator.RoutesCreator;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TeratorExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeratorExecutor.class);

    private final OsmImporter osmImporter;
    private final AggregatedTrafficBySegmentService aggregatedTrafficBySegmentService;
    private final TrajectoryListCreator trajectoryListCreator;
    private final RoutesCreator routesCreator;
    private final SimulationExecutor simulationExecutor;
    private final FixturesLocationMatcher fixturesLocationMatcher;
    private final AccuracyChecker accuracyChecker;


    public void execute(String osmFile, int startingNumberOfDay) {
        var city = osmImporter.importData(osmFile);
        var aggregatedTrafficBySegments = aggregatedTrafficBySegmentService.getAggregatedTrafficInWeekdaysBySegments();

        var distinctDays = aggregatedTrafficBySegments.values().stream()
                .flatMap(a -> a.stream().map(AggregatedTrafficBySegment::getDate))
                .distinct()
                .sorted()
                .toList();
        // only one day
        var allBuildingsByType = getBuildingsByType(city);
        var resource = TeratorExecutor.class.getClassLoader()
                .getResourceAsStream("var.tsv");
        var probabilities = ProbabilitiesToVariables.getProbabilities(extractVariables(resource));

        IntStream.range(startingNumberOfDay, startingNumberOfDay + 40)
                .forEach(numberOfDay ->
                        executeForSingleDay(numberOfDay, city, aggregatedTrafficBySegments, distinctDays,
                                allBuildingsByType, probabilities));
    }

    private void executeForSingleDay(int numberOfDay, City city,
                                     Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments,
                                     List<LocalDate> distinctDays,
                                     Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType,
                                     Probabilities probabilities
    ) {
        var trajectories = trajectoryListCreator.createTrajectories(probabilities, city, allBuildingsByType);
        var routesWithStartTime = routesCreator.createRoutesWithStartTimeInThreads(
                city,
                trajectories.singleTrajectories(),
                20
        );
        var onlyOneDay = getDataFromOneDay(numberOfDay, aggregatedTrafficBySegments, distinctDays);

        var simulationResult = simulationExecutor.executeSimulation(routesWithStartTime);
        var accuracy = checkSimulationResult(simulationResult, onlyOneDay);
        LOGGER.info("Mean squared error, number of day: {} ({}), error: {}", numberOfDay, distinctDays.get(numberOfDay),
                accuracy.meanSquaredError());
    }

    private Map<Integer, Set<AggregatedTrafficBySegment>> getDataFromOneDay(int numberOfDay,
                                                                            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments,
                                                                            List<LocalDate> distinctDays
    ) {
        return aggregatedTrafficBySegments.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                a -> a.getValue().stream().filter(s -> s.getDate().equals(distinctDays.get(numberOfDay)))
                        .collect(Collectors.toSet())
        ));
    }

    private List<Double> extractVariables(InputStream resource) {
        try {
            var result = IOUtils.toString(resource, StandardCharsets.UTF_8);
            return Arrays.stream(result.split("\t")).map(Double::valueOf).toList();
        } catch (IOException e) {
            LOGGER.error("Cannot extract variables from file. Using random.", e);
            Random r = new Random();
            return IntStream.range(0, 745)
                    .mapToDouble(_i -> r.nextDouble())
                    .boxed().collect(Collectors.toList());
        }
    }

    private GeneratedTrajectoriesAccuracy checkSimulationResult(SimulationResult simulationResult,
                                                                Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments
    ) {
        var detectorLocationToSimulationSegment =
                fixturesLocationMatcher.findAllMatchingSegmentsToDetectors(
                        simulationResult.simulationState().state().keySet()
                );
        return accuracyChecker.checkAccuracy(
                simulationResult, aggregatedTrafficBySegments, detectorLocationToSimulationSegment
        );
    }

    private Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> getBuildingsByType(City city) {
        return Arrays.stream(BuildingType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        buildingType -> buildingType.getEntitiesProvider().apply(city)
                ));
    }
}
