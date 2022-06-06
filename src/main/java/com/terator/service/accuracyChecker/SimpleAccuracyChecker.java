package com.terator.service.accuracyChecker;

import com.terator.model.AccuracyInSegment;
import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.SimulationResult;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.model.inductionLoops.DetectorLocation;
import com.terator.model.simulation.DensityInTime;
import com.terator.service.inductionLoops.InductionLoopsDataExtractor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimpleAccuracyChecker implements AccuracyChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccuracyChecker.class);
    private final InductionLoopsDataExtractor inductionLoopsDataExtractor;

    @Override
    public GeneratedTrajectoriesAccuracy checkAccuracy(
            SimulationResult simulationResult,
            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments
    ) {
        LOGGER.info("Starting checking accuracy");
        var detectorsWithLocations = inductionLoopsDataExtractor.extractData();
        var simulationState = simulationResult.simulationState().state();
        var existingSegmentsFromSimulation = simulationState.keySet();
        var fixturesLocationMatcher = new FixturesLocationMatcher(existingSegmentsFromSimulation);

        var resultsFromSegments = detectorsWithLocations.stream()
                .map(detectorWithLocations -> {
                    var locationsToBeMatched = detectorWithLocations.locationOfFixtures();
                    var matchedSegment2 = fixturesLocationMatcher.findMostMatchingSegment(locationsToBeMatched);
                    return matchedSegment2.map(matchedSegment -> {
                        var dataFromSimulation = simulationState.get(matchedSegment);
                        var dataFromInductionLoops =
                                aggregatedTrafficBySegments.get(detectorWithLocations.segmentId());

                        return compareDataFromSimulationWithRealData(dataFromSimulation, dataFromInductionLoops);
                    });
                })
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        return new GeneratedTrajectoriesAccuracy(resultsFromSegments);
    }

    private AccuracyInSegment compareDataFromSimulationWithRealData(
            DensityInTime dataFromSimulation,
            Set<AggregatedTrafficBySegment> dataFromInductionLoops
    ) {
        // todo
        return new AccuracyInSegment();
    }

    private void printAllLocations(Set<DetectorLocation> detectorsWithLocations) {
        detectorsWithLocations.forEach(detectorWithLocations -> {
            var locationsToBeMatched = detectorWithLocations.locationOfFixtures();

            Random obj = new Random();
            int rand_num = obj.nextInt(0xffffff + 1);
            String colorCode = String.format("#%06x", rand_num);

            locationsToBeMatched.forEach(
                    location -> System.out.println(
                            location.latitude() + "," + location.longitude() + "," + colorCode));
        });
    }

}
