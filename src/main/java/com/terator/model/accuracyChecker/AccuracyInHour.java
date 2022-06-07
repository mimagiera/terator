package com.terator.model.accuracyChecker;

public record AccuracyInHour(
        long countFromSimulation,
        double averageCountFromInductionLoops
) {
}
