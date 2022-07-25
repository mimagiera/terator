package com.terator.metaheuristic;

import com.terator.model.generatorTable.FromBuildingTypeGenerator;
import com.terator.model.generatorTable.PerfectDistancesFromBuilding;
import com.terator.model.generatorTable.Probabilities;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuilding;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime;
import com.terator.service.generatorCreator.building.BuildingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

class ProbabilitiesToVariablesTest {

    @Test
    void conversionBothWaysCreatesTheSameResult() {
        Probabilities probabilities = new Probabilities(Map.of(
                BuildingType.HOUSE, getBuildingTypeGenerator(),
                BuildingType.OFFICE, getBuildingTypeGenerator(),
                BuildingType.SERVICES, getBuildingTypeGenerator(),
                BuildingType.SCHOOL, getBuildingTypeGenerator(),
                BuildingType.CITY_EDGE_POINT, getBuildingTypeGenerator()
        ));

        var convertedVariables = ProbabilitiesToVariables.getVariables(probabilities);
        var convertedProbabilities = ProbabilitiesToVariables.getProbabilities(convertedVariables);

        Assertions.assertEquals(probabilities, convertedProbabilities);
    }

    private FromBuildingTypeGenerator getBuildingTypeGenerator() {
        return new FromBuildingTypeGenerator(
                new ProbabilitiesAndNumberOfDrawsFromBuilding(createProbabilitiesInTime()),
                new PerfectDistancesFromBuilding(Map.of(
                        BuildingType.HOUSE, 400,
                        BuildingType.OFFICE, 500,
                        BuildingType.SERVICES, 500,
                        BuildingType.SCHOOL, 500,
                        BuildingType.CITY_EDGE_POINT, 10
                ))
        );
    }

    private Map<LocalTime, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> createProbabilitiesInTime() {
        Map<LocalTime, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> res = new HashMap<>();
        IntStream.range(0, 24)
                .forEach(hour -> {
                    var first = LocalTime.of(hour, 0);
                    res.put(first, new ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime(
                            Map.of(
                                    BuildingType.HOUSE, 0.1,
                                    BuildingType.OFFICE, 0.2,
                                    BuildingType.SERVICES, 0.2,
                                    BuildingType.SCHOOL, 0.2,
                                    BuildingType.CITY_EDGE_POINT, 0.3
                            ),
                            hour
                    ));
                });
        return res;
    }

}