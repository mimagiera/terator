package com.terator.service.accuracyChecker;

import com.terator.model.AccuracyInSegment;
import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.SimulationResult;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.model.inductionLoops.DetectorLocation;
import com.terator.model.simulation.DensityInTime;
import com.terator.model.simulation.SimulationSegment;
import com.terator.service.inductionLoops.InductionLoopsDataExtractor;
import com.terator.service.inductionLoopsWithOsm.FixturesLocationMatcher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimpleAccuracyChecker implements AccuracyChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccuracyChecker.class);
    private final InductionLoopsDataExtractor inductionLoopsDataExtractor;

    @Override
    public GeneratedTrajectoriesAccuracy checkAccuracy(
            SimulationResult simulationResult,
            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments,
            Map<DetectorLocation, SimulationSegment> detectorLocationToSimulationSegment
    ) {
        LOGGER.info("Starting checking accuracy");
        var simulationState = simulationResult.simulationState().state();

        var resultsFromSegments = detectorLocationToSimulationSegment.entrySet().stream()
                .map(detectorsWithMappedSegments -> {
                    var detectorWithLocations = detectorsWithMappedSegments.getKey();
                    var matchedSegment = detectorsWithMappedSegments.getValue();

                    var dataFromSimulation = simulationState.get(matchedSegment);
                    var dataFromInductionLoops =
                            aggregatedTrafficBySegments.get(detectorWithLocations.segmentId());

                    return compareDataFromSimulationWithRealData(dataFromSimulation, dataFromInductionLoops);
                })
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
