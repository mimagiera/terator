package com.terator.service.generatorCreator.building;

import com.terator.model.generatorTable.FromBuildingTypeGenerator;
import com.terator.model.generatorTable.PerfectDistancesFromBuilding;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuilding;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import static com.terator.service.TeratorExecutor.MINUTES_INTERVAL_GENERATOR;

public class FromHouseStrategy implements FromBuildingTypeStrategy {
    @Override
    public FromBuildingTypeGenerator createGenerator() {
        return new FromBuildingTypeGenerator(
                new ProbabilitiesAndNumberOfDrawsFromBuilding(createProbabilitiesInTime()),
                new PerfectDistancesFromBuilding(Map.of(
                        BuildingType.HOUSE, 2.3,
                        BuildingType.OFFICE, 4.5
                ))
        );
    }

    private Map<LocalTime, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> createProbabilitiesInTime() {
        Map<LocalTime, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> res = new HashMap<>();
        var repeats = 24 * 60 / MINUTES_INTERVAL_GENERATOR;
        IntStream.range(0, repeats)
                .forEach(i -> {
                    var minute = i * MINUTES_INTERVAL_GENERATOR;
                    int hour = minute / 60;
                    int minuteOfHour = minute % 60;
                    var first = LocalTime.of(hour, minuteOfHour);
                    res.put(first, new ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime(
                            Map.of(
                                    BuildingType.HOUSE, 0.2,
                                    BuildingType.OFFICE, 0.4
                            ),
                            new Random().nextInt(2)
                    ));
                });
        return res;
    }
}
