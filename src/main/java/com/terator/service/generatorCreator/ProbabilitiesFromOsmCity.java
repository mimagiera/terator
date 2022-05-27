package com.terator.service.generatorCreator;

import com.terator.model.generatorTable.Probabilities;
import com.terator.service.generatorCreator.building.BuildingType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProbabilitiesFromOsmCity implements GeneratorCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProbabilitiesFromOsmCity.class);

    @Override
    public Probabilities generateProbabilities() {
        LOGGER.info("Starting generating probabilities");

        var buildingTypeFromBuildingTypeGeneratorMap =
                Arrays.stream(BuildingType.values())
                        .collect(Collectors.toMap(
                                Function.identity(), BuildingType::getFromBuildingTypeGenerator
                        ));

        return new Probabilities(buildingTypeFromBuildingTypeGeneratorMap);
    }

}
