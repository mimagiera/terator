package com.terator.model.accuracyChecker;

import java.io.Serializable;

public record ResultToCompareInHour(
        long countFromSimulation,
        double averageCountFromInductionLoops
) implements Serializable {
}
