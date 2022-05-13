package com.terator.model.generatorTable;

import java.time.LocalTime;
import java.util.Map;

public record ProbabilitiesAndNumberOfDrawsFromBuilding(
        Map<LocalTime, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> probabilitiesInTime
) {
}
