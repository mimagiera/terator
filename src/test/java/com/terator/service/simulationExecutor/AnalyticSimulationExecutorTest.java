package com.terator.service.simulationExecutor;

import com.terator.model.simulation.NumberOfCarsInTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class AnalyticSimulationExecutorTest {

    @Autowired
    TimeCalculations timeCalculations;

    @Test
    void updateDensityDurationInSeconds() {
        NumberOfCarsInTime initialDensity = new NumberOfCarsInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofSeconds(20), 5);

        var expected = new NumberOfCarsInTime(Map.of(
                LocalTime.of(10, 0), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensity() {
        NumberOfCarsInTime initialDensity = new NumberOfCarsInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofMinutes(9), 5);

        var expected = new NumberOfCarsInTime(Map.of(
                LocalTime.of(10, 0), 1L,
                LocalTime.of(10, 5), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensityLessThenOneSlot() {
        NumberOfCarsInTime initialDensity = new NumberOfCarsInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 2), Duration.ofMinutes(1), 5);

        var expected = new NumberOfCarsInTime(Map.of(
                LocalTime.of(10, 0), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensityOverMidnight() {
        NumberOfCarsInTime initialDensity = new NumberOfCarsInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(23, 59), Duration.ofMinutes(5),
                5);

        var expected = new NumberOfCarsInTime(Map.of(
                LocalTime.of(23, 55), 1L,
                LocalTime.of(0, 0), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensityExactlySlots() {
        NumberOfCarsInTime initialDensity = new NumberOfCarsInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofMinutes(10),
                5);

        var expected = new NumberOfCarsInTime(Map.of(
                LocalTime.of(10, 0), 1L,
                LocalTime.of(10, 5), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensityWithInitialState() {
        final HashMap<LocalTime, Long> density = new HashMap<>();
        density.put(LocalTime.of(10, 0), 1L);
        density.put(LocalTime.of(9, 55), 1L);
        NumberOfCarsInTime initialDensity = new NumberOfCarsInTime(density);

        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofMinutes(9),
                5);

        var expected = new NumberOfCarsInTime(Map.of(
                LocalTime.of(9, 55), 1L,
                LocalTime.of(10, 0), 2L,
                LocalTime.of(10, 5), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }
}