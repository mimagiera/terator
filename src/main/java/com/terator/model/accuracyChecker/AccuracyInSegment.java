package com.terator.model.accuracyChecker;

import java.util.Map;

public record AccuracyInSegment(
        Map<Integer, AccuracyInHour> accuracyInHours,
        double accuracy
) {
}
