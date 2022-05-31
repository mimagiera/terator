package com.terator.model.inductionLoops;

import java.util.Set;

public record DetectorsWithSegmentId(
        Set<Integer> detectorIds,
        Integer segmentId
) {
}
