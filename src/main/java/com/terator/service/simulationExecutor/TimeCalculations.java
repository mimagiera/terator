package com.terator.service.simulationExecutor;

import com.terator.model.simulation.NumberOfCarsInTime;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.stream.IntStream;

import static com.terator.model.simulation.NumberOfCarsInTime.SECONDS_INTERVAL_SIMULATOR;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class TimeCalculations {

    NumberOfCarsInTime updateDensity(NumberOfCarsInTime previousDensity, LocalTime startTime, Duration timeInStep) {
        return updateDensity(previousDensity, startTime, timeInStep, SECONDS_INTERVAL_SIMULATOR);
    }

    NumberOfCarsInTime updateDensity(NumberOfCarsInTime previousDensity, LocalTime startTime, Duration timeInStep,
                                     int secondsIntervalSimulator
    ) {
        var simulationBasedTime = findTimeInSimulation(startTime, secondsIntervalSimulator);
        var initialDensity = previousDensity.numberOfCars();

        long numberOfUpdates =
                calculateNumberOfUpdates(startTime, timeInStep, secondsIntervalSimulator, simulationBasedTime);

        IntStream.range(0, Math.toIntExact(numberOfUpdates))
                .forEach(i -> {
                    var timeToBeUpdated = simulationBasedTime.plusSeconds((long) i * secondsIntervalSimulator);
                    initialDensity.put(timeToBeUpdated, initialDensity.getOrDefault(timeToBeUpdated, 0L) + 1);
                });

        return new NumberOfCarsInTime(initialDensity);
    }

    private long calculateNumberOfUpdates(LocalTime startTime, Duration timeInStep, int secondsIntervalSimulator,
                                          LocalTime simulationBasedTime
    ) {
        var timeDifference = SECONDS.between(simulationBasedTime, startTime);
        var secondsToLast = timeInStep.toSeconds() + timeDifference;
        return (long) Math.ceil((double) secondsToLast / (secondsIntervalSimulator));
    }

    LocalTime findTimeInSimulation(LocalTime time) {
        return findTimeInSimulation(time, SECONDS_INTERVAL_SIMULATOR);
    }

    /**
     * @param time in which car enters the segment
     * @return time in which simulation state is holded
     */
    LocalTime findTimeInSimulation(LocalTime time, int secondsIntervalSimulator) {
        var seconds = time.getHour() * 60 * 60 + time.getMinute() * 60 + time.getSecond();

        int timeInSimulationSeconds = (seconds / secondsIntervalSimulator) * secondsIntervalSimulator;

        final int hour = timeInSimulationSeconds / 3600;
        final int minute = (timeInSimulationSeconds / 60) % 60;
        final int second = timeInSimulationSeconds % 60;
        return LocalTime.of(hour, minute, second);
    }
}
