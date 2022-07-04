package com.terator.service.simulationExecutor;

import com.terator.model.simulation.NumberOfCarsInTime;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.stream.IntStream;

import static com.terator.model.simulation.NumberOfCarsInTime.MINUTES_INTERVAL_SIMULATOR;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class TimeCalculations {

    NumberOfCarsInTime updateDensity(NumberOfCarsInTime previousDensity, LocalTime startTime, Duration timeInStep) {
        return updateDensity(previousDensity, startTime, timeInStep, MINUTES_INTERVAL_SIMULATOR);
    }

    NumberOfCarsInTime updateDensity(NumberOfCarsInTime previousDensity, LocalTime startTime, Duration timeInStep,
                                     int minutesIntervalSimulator
    ) {
        var simulationBasedTime = findTimeInSimulation(startTime, minutesIntervalSimulator);
        var initialDensity = previousDensity.numberOfCars();

        long numberOfUpdates =
                calculateNumberOfUpdates(startTime, timeInStep, minutesIntervalSimulator, simulationBasedTime);

        IntStream.range(0, Math.toIntExact(numberOfUpdates))
                .forEach(i -> {
                    var timeToBeUpdated = simulationBasedTime.plusMinutes((long) i * minutesIntervalSimulator);
                    initialDensity.put(timeToBeUpdated, initialDensity.getOrDefault(timeToBeUpdated, 0L) + 1);
                });

        return new NumberOfCarsInTime(initialDensity);
    }

    private long calculateNumberOfUpdates(LocalTime startTime, Duration timeInStep, int minutesIntervalSimulator,
                                          LocalTime simulationBasedTime
    ) {
        var timeDifference = SECONDS.between(simulationBasedTime, startTime);
        var secondsToLast = timeInStep.toSeconds() + timeDifference;
        return (long) Math.ceil((double) secondsToLast / (minutesIntervalSimulator * 60));
    }

    LocalTime findTimeInSimulation(LocalTime time) {
        return findTimeInSimulation(time, MINUTES_INTERVAL_SIMULATOR);
    }

    /**
     * @param time in which car enters the segment
     * @return time in which simulation state is holded
     */
    LocalTime findTimeInSimulation(LocalTime time, int minutesIntervalSimulator) {
        var minutes = time.getHour() * 60 + time.getMinute();

        int timeInSimulationMinutes = (minutes / minutesIntervalSimulator) * minutesIntervalSimulator;

        return LocalTime.of(timeInSimulationMinutes / 60, timeInSimulationMinutes % 60);
    }
}
