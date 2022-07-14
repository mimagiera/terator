package com.terator.metaheuristic;

import com.google.common.collect.Lists;
import com.terator.model.generatorTable.FromBuildingTypeGenerator;
import com.terator.model.generatorTable.PerfectDistancesFromBuilding;
import com.terator.model.generatorTable.Probabilities;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuilding;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime;
import com.terator.service.generatorCreator.building.BuildingType;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static com.terator.service.generatorCreator.building.BuildingType.CITY_EDGE_POINT;
import static com.terator.service.generatorCreator.building.BuildingType.HOUSE;
import static com.terator.service.generatorCreator.building.BuildingType.OFFICE;

public final class ProbabilitiesToVariables {

    public static final List<BuildingType> BUILDING_TYPES_WITH_ORDER = List.of(HOUSE, OFFICE, CITY_EDGE_POINT);
    public static final int PROBABILITIES_IN_TIME_SIZE = 24 * (BUILDING_TYPES_WITH_ORDER.size() + 1);
    public static final int FROM_ONE_TYPE_NUMBER_OF_VARIABLES =
            PROBABILITIES_IN_TIME_SIZE + BUILDING_TYPES_WITH_ORDER.size();

    public static List<Double> getVariables(Probabilities probabilities) {
        var result = new LinkedList<Double>();

        var buildingTypeFromBuildingTypeGeneratorMap =
                probabilities.buildingTypeFromBuildingTypeGeneratorMap();

        BUILDING_TYPES_WITH_ORDER.forEach(buildingType -> {
            var fromBuildingTypeGenerator = buildingTypeFromBuildingTypeGeneratorMap.get(buildingType);

            // probabilitiesInTime
            var probabilitiesInTime =
                    fromBuildingTypeGenerator.probabilitiesAndNumberOfDrawsFromBuilding().probabilitiesInTime();

            SortedSet<LocalTime> sortedTimes = new TreeSet<>(probabilitiesInTime.keySet());
            for (LocalTime localTime : sortedTimes) {
                var probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime = probabilitiesInTime.get(localTime);
                // probabilityToType
                var probabilityToType = probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime.probabilityToType();
                BUILDING_TYPES_WITH_ORDER.forEach(buildingType1 -> {
                    var probabilityToBuilding = probabilityToType.get(buildingType1);
                    result.add(probabilityToBuilding);
                });

                // expectedNumberOfDraws
                var expectedNumberOfDraws =
                        probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime.expectedNumberOfDraws();
                result.add(expectedNumberOfDraws);
            }

            // expectedDistancesToBuildingTypes
            var expectedDistancesToBuildingTypes =
                    fromBuildingTypeGenerator.perfectDistancesFromBuilding().expectedDistancesToBuildingTypes();
            BUILDING_TYPES_WITH_ORDER.forEach(buildingType1 -> {
                var expectedDistanceToBuilding = expectedDistancesToBuildingTypes.get(buildingType1);
                result.add(Double.valueOf(expectedDistanceToBuilding));
            });
        });

        return result;
    }

    public static Probabilities getProbabilities(List<Double> variables) {
        var variablesDividedByBuildingTypes = Lists.partition(variables, FROM_ONE_TYPE_NUMBER_OF_VARIABLES);
        Map<BuildingType, FromBuildingTypeGenerator> buildingTypeFromBuildingTypeGeneratorMap = new HashMap<>();

        IntStream.range(0, BUILDING_TYPES_WITH_ORDER.size())
                .forEach(i -> {

                    var buildingType = BUILDING_TYPES_WITH_ORDER.get(i);
                    var variablesFromThisType = variablesDividedByBuildingTypes.get(i);

                    // probabilitiesAndNumberOfDrawsFromBuilding

                    Map<LocalTime, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> probabilitiesInTime =
                            new HashMap<>();
                    IntStream.range(0, 24)
                            .forEach(hour -> {
                                var localTime = LocalTime.of(hour, 0);
                                Map<BuildingType, Double> probabilityToType = new HashMap<>();
                                IntStream.range(0, BUILDING_TYPES_WITH_ORDER.size())
                                        .forEach(toBuildingNumberProbability -> {
                                            int indexOfProbability =
                                                    hour * (BUILDING_TYPES_WITH_ORDER.size() + 1) +
                                                            toBuildingNumberProbability;
                                            probabilityToType.put(
                                                    BUILDING_TYPES_WITH_ORDER.get(toBuildingNumberProbability),
                                                    variablesFromThisType.get(indexOfProbability)
                                            );
                                        });

                                int indexOfExpectedNumberOfDraws =
                                        hour * (BUILDING_TYPES_WITH_ORDER.size() + 1) +
                                                BUILDING_TYPES_WITH_ORDER.size();

                                var probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime =
                                        new ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime(
                                                probabilityToType,
                                                variablesFromThisType.get(indexOfExpectedNumberOfDraws)
                                        );
                                probabilitiesInTime.put(
                                        localTime,
                                        probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime
                                );
                            });

                    // perfectDistancesFromBuilding
                    Map<BuildingType, Integer> expectedDistancesToBuildingTypes = new HashMap<>();
                    IntStream.range(0, BUILDING_TYPES_WITH_ORDER.size())
                            .forEach(toBuildingNumberDistance -> expectedDistancesToBuildingTypes.put(
                                    BUILDING_TYPES_WITH_ORDER.get(toBuildingNumberDistance),
                                    variablesFromThisType.get(PROBABILITIES_IN_TIME_SIZE + toBuildingNumberDistance)
                                            .intValue()
                            ));

                    var fromBuildingTypeGenerator = new FromBuildingTypeGenerator(
                            new ProbabilitiesAndNumberOfDrawsFromBuilding(probabilitiesInTime),
                            new PerfectDistancesFromBuilding(expectedDistancesToBuildingTypes)
                    );
                    buildingTypeFromBuildingTypeGeneratorMap.put(buildingType, fromBuildingTypeGenerator);
                });

        return new Probabilities(buildingTypeFromBuildingTypeGeneratorMap);
    }
}
