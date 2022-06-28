package com.terator.service.accuracyChecker;

import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.SimulationResult;
import com.terator.model.accuracyChecker.ResultToCompareInHour;
import com.terator.model.accuracyChecker.AccuracyInSegment;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.model.inductionLoops.DetectorLocation;
import com.terator.model.simulation.DensityInTime;
import com.terator.model.simulation.SimulationSegment;
import com.terator.service.inductionLoops.InductionLoopsDataExtractor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        var dataFromInductionLoopsPerHour = dataFromInductionLoops.stream()
                .collect(Collectors.groupingBy(
                        AggregatedTrafficBySegment::getHour,
                        Collectors.mapping(AggregatedTrafficBySegment::getCount, Collectors.averagingInt(a -> a))
                ));

        var dataFromSimulationPerHour = dataFromSimulation.density()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        localTimeLongEntry -> localTimeLongEntry.getKey().getHour(),
                        Map.Entry::getValue,
                        Long::sum
                ));

        var resultsInHours = IntStream.rangeClosed(0, 23)
                .boxed()
                .collect(Collectors.toMap(
                        Function.identity(),
                        hour -> new ResultToCompareInHour(
                                dataFromSimulationPerHour.getOrDefault(hour, 0L),
                                dataFromInductionLoopsPerHour.getOrDefault(hour, 0d)
                        )
                ));

        var accuracy = resultsInHours.values().stream()
                .map(accuracyInHour -> {
                    var fromSimulation = accuracyInHour.countFromSimulation();
                    var fromInductionLoops = accuracyInHour.averageCountFromInductionLoops();
                    if (fromInductionLoops != 0) {
                        return 100 - Math.abs(fromSimulation - fromInductionLoops) / fromInductionLoops * 100;
                    } else {
                        return 100d;
                    }
                })
                .mapToDouble(a -> a)
                .average()
                .getAsDouble();

        return new AccuracyInSegment(resultsInHours, accuracy);
    }

    private void printAllLocations(Set<DetectorLocation> detectorsWithLocations) {
        detectorsWithLocations.forEach(detectorWithLocations -> {
            var locationsToBeMatched = detectorWithLocations.locationOfFixtures();

            Random obj = new Random();
            int rand_num = obj.nextInt(0xffffff + 1);
            String colorCode = String.format("#%06x", rand_num);

            locationsToBeMatched.forEach(
                    location -> System.out.println(
                            location.getLatitude().asDegrees() + "," + location.getLongitude().asDegrees() + "," +
                                    colorCode));
        });
    }

}
