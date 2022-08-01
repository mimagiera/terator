package com.terator.service;

import com.terator.metaheuristic.FindBestGeneratorVariables;
import com.terator.model.City;
import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.service.generatorCreator.building.BuildingType;
import com.terator.service.inductionLoops.AggregatedTrafficBySegmentService;
import com.terator.service.osmImporter.OsmImporter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeratorExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeratorExecutor.class);
    public static final int MINUTES_INTERVAL_GENERATOR = 60;

    private final OsmImporter osmImporter;

    private final AggregatedTrafficBySegmentService aggregatedTrafficBySegmentService;

    private final FindBestGeneratorVariables findBestGeneratorVariables;


    public DoubleSolution execute(String osmFile, int nThreads) {
        var city = osmImporter.importData(osmFile);
        var aggregatedTrafficBySegments = aggregatedTrafficBySegmentService.getAggregatedTrafficInWeekdaysBySegments();
        var allBuildingsByType = getBuildingsByType(city);

        return findBestGeneratorVariables.doEverything(city, allBuildingsByType, aggregatedTrafficBySegments, nThreads);
    }

    private Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> getBuildingsByType(City city) {
        return Arrays.stream(BuildingType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        buildingType -> buildingType.getEntitiesProvider().apply(city)
                ));
    }

    public static void printElapsedTime(long start, long end, String message, Logger logger) {
        float sec = (end - start) / 1000F;
        logger.info(" Elapsed {} seconds: {}", message, sec);
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
