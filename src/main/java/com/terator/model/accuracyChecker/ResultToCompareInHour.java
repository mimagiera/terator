package com.terator.model.accuracyChecker;

public record ResultToCompareInHour(
        long countFromSimulation,
        double averageCountFromInductionLoops
) {
}
