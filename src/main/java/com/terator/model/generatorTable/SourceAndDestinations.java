package com.terator.model.generatorTable;

import com.terator.model.Area;

public record SourceAndDestinations(
        Area source,
        DestinationsWithProbabilityAndNumberOfDraws destinationsWithProbabilityAndNumberOfDraws
) {
}
