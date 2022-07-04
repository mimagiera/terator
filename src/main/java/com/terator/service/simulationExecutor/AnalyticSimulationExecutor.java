package com.terator.service.simulationExecutor;

import com.terator.model.City;
import com.terator.model.SimulationResult;
import com.terator.model.SingleTrajectory;
import com.terator.model.Trajectories;
import com.terator.model.simulation.NumberOfCarsInTime;
import com.terator.model.simulation.SimulationSegment;
import com.terator.model.simulation.SimulationState;
import lombok.RequiredArgsConstructor;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.geography.atlas.items.Route;
import org.openstreetmap.atlas.geography.atlas.routing.AStarRouter;
import org.openstreetmap.atlas.tags.LanesTag;
import org.openstreetmap.atlas.tags.MaxSpeedTag;
import org.openstreetmap.atlas.utilities.scalars.Distance;
import org.openstreetmap.atlas.utilities.scalars.Speed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.terator.model.PointOnMapOnRoad.DEFAULT_LANES_NUMBER;
import static com.terator.model.PointOnMapOnRoad.DEFAULT_MAX_SPEED;

@Service
@RequiredArgsConstructor
public class AnalyticSimulationExecutor implements SimulationExecutor {
    private static final int MAX_NUMBER_OF_CARS_PER_KILOMETER = 200;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticSimulationExecutor.class);
    public static final int MINIMAL_SPEED = 1;

    private final TimeCalculations timeCalculations;

    @Override
    public SimulationResult executeSimulation(City city, Trajectories trajectories) {
        LOGGER.info("Starting executing simulation");

        AtomicReference<SimulationState> simulationState = new AtomicReference<>(new SimulationState(new HashMap<>()));

        trajectories.singleTrajectories()
                .forEach(singleTrajectory ->
                        createRoute(singleTrajectory, city.atlas())
                                .ifPresent(
                                        route -> simulationState.set(
                                                simulateRoute(route, simulationState.get(),
                                                        singleTrajectory.startTime())
                                        )
                                ));

        return new SimulationResult(simulationState.get());
    }

    private Optional<Route> createRoute(SingleTrajectory singleTrajectory, Atlas atlas) {
        var startLocation = singleTrajectory.startLocation();
        var endLocation = singleTrajectory.endLocation();

        final Route route =
                AStarRouter.fastComputationAndSubOptimalRoute(atlas, Distance.MAXIMUM)
                        .route(startLocation, endLocation);

        return Optional.ofNullable(route);
    }

    private SimulationState simulateRoute(Route route, SimulationState simulationState, LocalTime startTime) {
        var edges = new LinkedList<Edge>();
        route.forEach(edges::add);

        var state = simulationState.state();
        var timeThatCarEnterSegment = startTime;

        for (Edge edge : edges) {
            var simulationSegment = new SimulationSegment(edge);
            var numberOfCarsInTime = state.getOrDefault(simulationSegment, new NumberOfCarsInTime(new HashMap<>()));

            var timeInSegment = durationBasedOnLengthAndNumberOfLanes(
                    simulationSegment,
                    numberOfCarsInTime,
                    timeThatCarEnterSegment
            );

            // update map
            var updatedDensity = timeCalculations.updateDensity(numberOfCarsInTime, timeThatCarEnterSegment,
                    timeInSegment);
            state.put(simulationSegment, updatedDensity);

            timeThatCarEnterSegment = timeThatCarEnterSegment.plus(timeInSegment);
        }
        return new SimulationState(state);
    }

    /**
     * calculate how long vehicle could move between two nodes if there was no traffic etc.
     * Uses static road info: edge length and max speed
     */
    private Duration durationBasedOnLengthAndNumberOfLanes(SimulationSegment simulationSegment,
                                                           NumberOfCarsInTime numberOfCarsInTime,
                                                           LocalTime timeThatCarEnterSegment
    ) {
        final Edge edge = simulationSegment.edge();
        var edgeLengthKilometers = edge.length().asKilometers();
        var maxSpeedKilometersPerHour = MaxSpeedTag.get(edge).map(Speed::asKilometersPerHour).orElse(DEFAULT_MAX_SPEED);
        var lanesNumber = LanesTag.numberOfLanes(edge).orElse(DEFAULT_LANES_NUMBER);

        var lengthWithLanesKilometers = edgeLengthKilometers * lanesNumber;

        var simulationBasedTime = timeCalculations.findTimeInSimulation(timeThatCarEnterSegment);
        var currentNumberOfCars = numberOfCarsInTime.numberOfCars().getOrDefault(simulationBasedTime, 0L);
        var currentNumberOfCarsPerKilometer =
                currentNumberOfCars / lengthWithLanesKilometers; // cars per meter on a road

        var speedFromEquation =
                maxSpeedKilometersPerHour * (1 - currentNumberOfCarsPerKilometer / MAX_NUMBER_OF_CARS_PER_KILOMETER);

        var calculatedSpeed = speedFromEquation <= 0 ? MINIMAL_SPEED : speedFromEquation;

        var timeInSeconds = edgeLengthKilometers / calculatedSpeed * 3600;
        return Duration.ofSeconds((long) timeInSeconds);
    }

}
