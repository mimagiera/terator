package com.terator.model.accuracyChecker;

import java.util.Map;

public record AccuracyInSegment(
        Map<Integer, ResultToCompareInHour> accuracyInHours,
        double accuracy
) {
}
