package com.terator.model.generatorTable;

import java.time.Instant;
import java.util.Map;

public record ProbabilitiesAndNumberOfDrawsFromBuilding(
        Map<Instant, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> probabilitiesInTime
) {
}
