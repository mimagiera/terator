package com.terator.service.accuracyChecker;

import com.terator.model.AccuracyInSegment;
import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.Location;
import com.terator.model.SimulationResult;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.model.simulation.DensityInTime;
import com.terator.model.simulation.SimulationSegment;
import com.terator.service.inductionLoops.AggregatedTrafficBySegmentService;
import com.terator.service.inductionLoops.InductionLoopsDataExtractor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimpleAccuracyChecker implements AccuracyChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccuracyChecker.class);
    private final InductionLoopsDataExtractor inductionLoopsDataExtractor;
    private final AggregatedTrafficBySegmentService aggregatedTrafficBySegmentService;

    @Override
    public GeneratedTrajectoriesAccuracy checkAccuracy(SimulationResult simulationResult
    ) {
        LOGGER.info("Starting checking accuracy");
        var detectorsWithLocations = inductionLoopsDataExtractor.extractData();
        var simulationState = simulationResult.simulationState().state();
        var existingSegmentsFromSimulation = simulationState.keySet();

        var resultsFromSegments = detectorsWithLocations.stream().map(detectorWithLocations -> {
                    var locationsToBeMatched = detectorWithLocations.locationOfFixtures();
                    var matchedSegments = findMatchingSegments(locationsToBeMatched,
                            existingSegmentsFromSimulation);

                    locationsToBeMatched.forEach(
                            location -> System.out.println(location.latitude() + "," + location.longitude() + ",#00FF01"));
                    System.out.println();

                    var dataFromSimulation = matchedSegments.stream()
                            .map(simulationState::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

//                    var dataFromInductionLoops = extractDataInDetectors(detectorWithLocations.segmentId());
                    // compare

                    return compareDataFromSimulationWithRealData(dataFromSimulation, Set.of());
                })
                .collect(Collectors.toSet());

        return new GeneratedTrajectoriesAccuracy(resultsFromSegments);
    }

    private Set<SimulationSegment> findMatchingSegments(
            Set<Location> locationsFromInductionLoopData,
            Set<SimulationSegment> existingSegmentsFromSimulation
    ) {
        // todo
        return Set.of();
    }

    private AccuracyInSegment compareDataFromSimulationWithRealData(
            Set<DensityInTime> dataFromSimulation,
            Set<Iterable<AggregatedTrafficBySegment>> dataFromInductionLoops
    ) {
        // todo
        return new AccuracyInSegment();
    }

    private Iterable<AggregatedTrafficBySegment> extractDataInDetectors(Integer segmentId) {
        return aggregatedTrafficBySegmentService.getBySegmentId(segmentId);
    }

}
