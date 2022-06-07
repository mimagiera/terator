package com.terator.model;

import com.terator.model.accuracyChecker.AccuracyInSegment;

import java.util.Set;

public record GeneratedTrajectoriesAccuracy(
        Set<AccuracyInSegment> accuracyInSegments
) {
}
