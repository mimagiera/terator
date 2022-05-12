package com.terator.service.generatorCreator.building;

import com.terator.model.generatorTable.FromBuildingTypeGenerator;
import com.terator.model.generatorTable.PerfectDistancesFromBuilding;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuilding;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime;

import java.time.Instant;
import java.util.Map;

public final class FromOfficeStrategy implements FromBuildingTypeStrategy {
    @Override
    public FromBuildingTypeGenerator createGenerator() {
        return new FromBuildingTypeGenerator(
                new ProbabilitiesAndNumberOfDrawsFromBuilding(
                        Map.of(
                                Instant.now(),
                                new ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime(Map.of(), 4L))
                ),
                new PerfectDistancesFromBuilding(Map.of())
        );
    }
}
