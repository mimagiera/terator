package com.terator.service.generatorCreator;

import com.terator.model.City;
import com.terator.model.generatorTable.Probabilities;

public interface GeneratorCreator {
    Probabilities generateProbabilities(City city);
}
