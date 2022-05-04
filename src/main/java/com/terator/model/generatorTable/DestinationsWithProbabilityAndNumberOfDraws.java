package com.terator.model.generatorTable;

import java.util.Set;

public record DestinationsWithProbabilityAndNumberOfDraws(
        Set<ProbabilityToArea> probabilitiesToAreas,
        Long numberOfDraws
) {
}
