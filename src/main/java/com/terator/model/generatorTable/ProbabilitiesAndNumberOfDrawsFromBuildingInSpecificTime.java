package com.terator.model.generatorTable;

import com.terator.service.generatorCreator.building.BuildingType;

import java.util.Map;

public record ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime(
        Map<BuildingType, Double> probabilityToType,
        double expectedNumberOfDraws
) {
}
