package com.terator.model.generatorTable;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public record Probabilities(
        Map<Instant, Set<SourceAndDestinations>> sourcesAndDestinationsInTime
) {
}
