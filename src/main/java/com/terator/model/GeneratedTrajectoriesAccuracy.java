package com.terator.model;

import com.terator.model.accuracyChecker.AccuracyInSegment;

import java.io.Serializable;
import java.util.Set;

public record GeneratedTrajectoriesAccuracy(
        Set<AccuracyInSegment> accuracyInSegments,
        double meanSquaredError
) implements Serializable {
}
