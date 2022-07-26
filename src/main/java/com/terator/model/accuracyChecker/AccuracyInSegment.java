package com.terator.model.accuracyChecker;

import java.io.Serializable;
import java.util.Map;

public record AccuracyInSegment(
        Map<Integer, ResultToCompareInHour> accuracyInHours,
        double accuracy
) implements Serializable {
}
