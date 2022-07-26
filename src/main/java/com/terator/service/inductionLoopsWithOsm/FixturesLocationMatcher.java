package com.terator.service.inductionLoopsWithOsm;

import com.terator.model.inductionLoops.DetectorLocation;
import com.terator.model.inductionLoopsWithOsm.SimulationSegmentWithDistance;
import com.terator.model.simulation.SimulationSegment;
import com.terator.service.inductionLoops.InductionLoopsDataExtractor;
import lombok.RequiredArgsConstructor;
import org.openstreetmap.atlas.geography.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixturesLocationMatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(FixturesLocationMatcher.class);
    private static final int MAX_POSSIBLE_AVERAGE_IN_DISTANCES = 200;

    private final InductionLoopsDataExtractor inductionLoopsDataExtractor;

    /**
     * @return matched detectors to simulation segments if average of distances is less than 200 meters
     */
    public Map<DetectorLocation, SimulationSegment> findAllMatchingSegmentsToDetectors(
            Set<SimulationSegment> existingSegmentsFromSimulation
    ) {
        if (existingSegmentsFromSimulation.isEmpty()) {
            return Map.of();
        } else {
            var detectorsWithLocations = inductionLoopsDataExtractor.extractData();
            return findMatchingSegmentsToDetectors(detectorsWithLocations, existingSegmentsFromSimulation);
        }
    }

    private Map<DetectorLocation, SimulationSegment> findMatchingSegmentsToDetectors(
            Set<DetectorLocation> detectorsWithLocations,
            Set<SimulationSegment> existingSegmentsFromSimulation
    ) {
        return detectorsWithLocations.stream()
                .collect(Collectors.toMap(Function.identity(), detectorWithLocations -> {
                    var locationsToBeMatched = detectorWithLocations.locationOfFixtures();
                    return findMostMatchingSegment(locationsToBeMatched, existingSegmentsFromSimulation);
                }))
                .entrySet().stream()
                .filter(detectorLocationOptionalEntry -> detectorLocationOptionalEntry.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, f -> f.getValue().get()));
    }

    private Optional<SimulationSegment> findMostMatchingSegment(
            Set<Location> locationsFromInductionLoopData,
            Set<SimulationSegment> existingSegmentsFromSimulation
    ) {
        var closestSimulationSegmentsToLocationWithDistances = locationsFromInductionLoopData
                .stream()
                .map(location -> findClosestToLocation(existingSegmentsFromSimulation, location))
                .collect(Collectors.groupingBy(
                        SimulationSegmentWithDistance::simulationSegment,
                        Collectors.mapping(SimulationSegmentWithDistance::distance, Collectors.toSet())
                ));

        var theMostOftenTheClosestSegmentWithDistances = closestSimulationSegmentsToLocationWithDistances
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(segmentWithDistances -> segmentWithDistances.getValue().size()))
                .get();

        final OptionalDouble average = theMostOftenTheClosestSegmentWithDistances.getValue().stream()
                .mapToDouble(a -> a)
                .average();
        if (
                average.stream()
                        .filter(a -> a < MAX_POSSIBLE_AVERAGE_IN_DISTANCES)
                        .average()
                        .isEmpty()) {
            LOGGER.debug("Excluded detector with average distance: {}", average.getAsDouble());
//            printLocations(locationsFromInductionLoopData, theMostOftenTheClosestSegmentWithDistances);
            return Optional.empty();
        }

//        printLocations(locationsFromInductionLoopData, theMostOftenTheClosestSegmentWithDistances);

//        LOGGER.info("The most often closest distances. [size, locations size]: [{}, {}] : {}",
//                theMostOftenTheClosestSegmentWithDistances.getValue().size(),
//                locationsFromInductionLoopData.size(),
//                theMostOftenTheClosestSegmentWithDistances.getValue());

        return Optional.of(theMostOftenTheClosestSegmentWithDistances.getKey());
    }

    private SimulationSegmentWithDistance findClosestToLocation(
            Set<SimulationSegment> existingSegmentsFromSimulation, Location location
    ) {
        return existingSegmentsFromSimulation
                .stream()
                .map(simulationSegment -> new SimulationSegmentWithDistance(
                        simulationSegment, distanceFromLocationToSimulationSegment(simulationSegment, location)
                ))
                .min(Comparator.comparingDouble(
                        SimulationSegmentWithDistance::distance
                ))
                .get();
    }

    private double distanceFromLocationToSimulationSegment(SimulationSegment simulationSegment, Location location) {
        var polyline = simulationSegment.edge().asPolyLine();
        var distance = location.snapTo(polyline).getDistance();
        return distance.asMeters();
    }

    private void printLocations(
            Set<Location> locationsFromInductionLoopData,
            Map.Entry<SimulationSegment, Set<Double>> theMostOftenTheClosestSegmentWithDistances
    ) {
        var locationStart = theMostOftenTheClosestSegmentWithDistances.getKey().edge().start().getLocation();
        var locationEnd = theMostOftenTheClosestSegmentWithDistances.getKey().edge().end().getLocation();
        System.out.println(
                locationStart.getLatitude().asDegrees() + "," + locationStart.getLongitude().asDegrees() + ",#ff0000");
        System.out.println(
                locationEnd.getLatitude().asDegrees() + "," + locationEnd.getLongitude().asDegrees() + ",#ff0000");
        locationsFromInductionLoopData.forEach(
                location -> System.out.println(
                        location.getLatitude().asDegrees() + "," + location.getLongitude().asDegrees() +
                                ",#75a481"));
    }
}
