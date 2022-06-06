package com.terator.service.accuracyChecker;

import com.terator.model.Location;
import com.terator.model.simulation.SimulationSegment;
import com.terator.service.trajectoryListCreator.LocationExtractor;
import lombok.RequiredArgsConstructor;
import org.openstreetmap.atlas.geography.PolyLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FixturesLocationMatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(FixturesLocationMatcher.class);

    private final Set<SimulationSegment> existingSegmentsFromSimulation;

    Optional<SimulationSegment> findMostMatchingSegment(Set<Location> locationsFromInductionLoopData) {
        var closestSimulationSegmentsToLocationWithDistances = locationsFromInductionLoopData
                .stream()
                .map(this::findClosestToLocation)
                .collect(Collectors.groupingBy(
                        SimulationSegmentWithDistance::simulationSegment,
                        Collectors.mapping(SimulationSegmentWithDistance::distance, Collectors.toSet())
                ));

        var theMostOftenTheClosestSegmentWithDistances = closestSimulationSegmentsToLocationWithDistances
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(segmentWithDistances -> segmentWithDistances.getValue().size()))
                .get();

        if (theMostOftenTheClosestSegmentWithDistances.getValue().stream().anyMatch(a -> a > 200)) {
            LOGGER.info("Excluded with minimal length: {}",
                    theMostOftenTheClosestSegmentWithDistances.getValue().stream().mapToDouble(a -> a).min()
                            .getAsDouble());
            return Optional.empty();
        }

        // print locations
        var locationStart = theMostOftenTheClosestSegmentWithDistances.getKey().startNode().getLocation();
        var locationEnd = theMostOftenTheClosestSegmentWithDistances.getKey().endNode().getLocation();
        System.out.println(
                locationStart.getLatitude().asDegrees() + "," + locationStart.getLongitude().asDegrees() + ",#ff0000");
        System.out.println(
                locationEnd.getLatitude().asDegrees() + "," + locationEnd.getLongitude().asDegrees() + ",#ff0000");
        locationsFromInductionLoopData.forEach(
                location -> System.out.println(location.latitude() + "," + location.longitude() + ",#75a481"));

        LOGGER.info("The most often closest distances. [size, locations size]: [{}, {}] : {}",
                theMostOftenTheClosestSegmentWithDistances.getValue().size(),
                locationsFromInductionLoopData.size(),
                theMostOftenTheClosestSegmentWithDistances.getValue());

        return Optional.of(theMostOftenTheClosestSegmentWithDistances.getKey());
    }

    private SimulationSegmentWithDistance findClosestToLocation(Location location) {
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
        var polyline = new PolyLine(
                simulationSegment.startNode().getLocation(),
                simulationSegment.endNode().getLocation()
        );
        var atlasLocation = LocationExtractor.fromTeratorLocation(location);
        var distance = atlasLocation.snapTo(polyline).getDistance();
        return distance.asMeters();
    }

}
