package com.terator.service.generatorCreator;

import com.terator.model.City;
import com.terator.model.generatorTable.Probabilities;
import com.terator.service.generatorCreator.strategies.ToChurchTrajectoriesGeneratorWithStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProbabilitiesFromOsmCity implements GeneratorCreator {
    private final ToChurchTrajectoriesGeneratorWithStrategy toChurchTrajectoriesGeneratorWithStrategy;

    @Override
    public Probabilities generateProbabilities(City city) {
        return toChurchTrajectoriesGeneratorWithStrategy.createProbabilities(city.entities());
    }

}
