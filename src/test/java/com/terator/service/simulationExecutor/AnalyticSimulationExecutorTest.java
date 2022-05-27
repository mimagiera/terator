package com.terator.service.simulationExecutor;

import com.terator.model.simulation.DensityInTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
        DensityInTime initialDensity = new DensityInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofSeconds(20), 5);

        var expected = new DensityInTime(Map.of(
                LocalTime.of(10, 0), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensity() {
        DensityInTime initialDensity = new DensityInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofMinutes(9), 5);

        var expected = new DensityInTime(Map.of(
                LocalTime.of(10, 0), 1L,
                LocalTime.of(10, 5), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensityLessThenOneSlot() {
        DensityInTime initialDensity = new DensityInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 2), Duration.ofMinutes(1), 5);

        var expected = new DensityInTime(Map.of(
                LocalTime.of(10, 0), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensityOverMidnight() {
        DensityInTime initialDensity = new DensityInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(23, 59), Duration.ofMinutes(5),
                5);

        var expected = new DensityInTime(Map.of(
                LocalTime.of(23, 55), 1L,
                LocalTime.of(0, 0), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }

    @Test
    void updateDensityExactlySlots() {
        DensityInTime initialDensity = new DensityInTime(new HashMap<>());
        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofMinutes(10),
                5);

        var expected = new DensityInTime(Map.of(
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
        DensityInTime initialDensity = new DensityInTime(density);

        var updated = timeCalculations.updateDensity(initialDensity, LocalTime.of(10, 0), Duration.ofMinutes(9),
                5);

        var expected = new DensityInTime(Map.of(
                LocalTime.of(9, 55), 1L,
                LocalTime.of(10, 0), 2L,
                LocalTime.of(10, 5), 1L
        ));

        Assertions.assertEquals(expected, updated);
    }
}