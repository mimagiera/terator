package com.terator.model.inductionLoops;

import org.openstreetmap.atlas.geography.Location;

import java.util.Set;

/**
 * holds location of detectors in one segment
 * all detectors with ids from detectorIds have location close to locationOfFixtures
 */
public record DetectorLocation(
        Set<Integer> detectorIds,
        Integer segmentId,
        Set<Location> locationOfFixtures
) {
}
