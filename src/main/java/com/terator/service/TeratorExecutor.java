package com.terator.service;

import com.terator.metaheuristic.FindBestGeneratorVariables;
import com.terator.model.City;
import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.generatorCreator.GeneratorCreator;
import com.terator.service.generatorCreator.building.BuildingType;
import com.terator.service.inductionLoops.AggregatedTrafficBySegmentService;
import com.terator.service.osmImporter.OsmImporter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

@Service
@RequiredArgsConstructor
public class TeratorExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeratorExecutor.class);
    public static final int MINUTES_INTERVAL_GENERATOR = 60;

    private final OsmImporter osmImporter;
    private final GeneratorCreator generatorCreator;

    private final AggregatedTrafficBySegmentService aggregatedTrafficBySegmentService;

    private final FindBestGeneratorVariables findBestGeneratorVariables;

    private static final Set<DayOfWeek> WEEK_DAYS_NO_WEEKEND = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);

    public GeneratedTrajectoriesAccuracy execute(String osmFile, int nThreads) {
        // executed once
        // generateInitialProbabilities
        long startProbabilities = System.currentTimeMillis();
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
        var allBuildingsByType = getBuildingsByType(city);
        long endFindingBuildingsWithTypes = System.currentTimeMillis();
        printElapsedTime(endGettingAggregatedDataFromInductionLoops, endFindingBuildingsWithTypes,
                "findingBuildingsWithTypes");

        findBestGeneratorVariables.doEverything(city, allBuildingsByType, aggregatedTrafficBySegments, nThreads);

        return null;
    }

    private Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> getBuildingsByType(City city) {
        return Arrays.stream(BuildingType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        buildingType -> buildingType.getEntitiesProvider().apply(city)
                ));
    }

    private Map<Integer, Set<AggregatedTrafficBySegment>> getAggregatedTrafficBySegments() {
        return StreamSupport.stream(aggregatedTrafficBySegmentService.getAll().spliterator(), false)
                .filter(traffic -> WEEK_DAYS_NO_WEEKEND.contains(traffic.getDate().getDayOfWeek()))
                .collect(Collectors.groupingBy(AggregatedTrafficBySegment::getSegmentId, Collectors.toSet()));
    }

    public static void printElapsedTime(long start, long end, String message) {
        float sec = (end - start) / 1000F;
        LOGGER.info(" Elapsed {} seconds: {}", message, sec);
    }

    private void findStats(City city,
                           Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType
    ) {
        var buildingsUsed =
                allBuildingsByType.entrySet().stream().filter(e -> !e.getKey().equals(BuildingType.CITY_EDGE_POINT))
                        .mapToInt(e -> e.getValue().size()).sum();

        var gr = city.entities().stream().map(e -> e.getOsmTags().get("building")).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        int a = 34;
    }
}
