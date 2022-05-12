package com.terator.service.generatorCreator;

import com.terator.model.generatorTable.Probabilities;
import com.terator.service.generatorCreator.building.BuildingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProbabilitiesFromOsmCity implements GeneratorCreator {

    @Override
    public Probabilities generateProbabilities() {
        var buildingTypeFromBuildingTypeGeneratorMap =
                Arrays.stream(BuildingType.values())
                        .collect(Collectors.toMap(
                                Function.identity(), BuildingType::getFromBuildingTypeGenerator
                        ));

        return new Probabilities(buildingTypeFromBuildingTypeGeneratorMap);
    }

}
