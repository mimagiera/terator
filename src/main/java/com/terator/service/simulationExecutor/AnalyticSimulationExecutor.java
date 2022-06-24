package com.terator.service.simulationExecutor;

import com.terator.model.City;
import com.terator.model.SimulationResult;
import com.terator.model.SingleTrajectory;
import com.terator.model.Trajectories;
import com.terator.model.simulation.DensityInTime;
import com.terator.model.simulation.SimulationSegment;
import com.terator.model.simulation.SimulationState;
import lombok.RequiredArgsConstructor;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.geography.atlas.items.Route;
import org.openstreetmap.atlas.geography.atlas.routing.AStarRouter;
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

@Service
@RequiredArgsConstructor
public class AnalyticSimulationExecutor implements SimulationExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticSimulationExecutor.class);

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
            var densityInSegment = state.getOrDefault(simulationSegment, new DensityInTime(new HashMap<>()));

            var timeInSegment = executeStep(simulationSegment, densityInSegment, timeThatCarEnterSegment);

            // update map
            var updatedDensity = timeCalculations.updateDensity(densityInSegment, timeThatCarEnterSegment,
                    timeInSegment);
            state.put(simulationSegment, updatedDensity);

            timeThatCarEnterSegment = timeThatCarEnterSegment.plus(timeInSegment);
        }
        return new SimulationState(state);
    }

    private Duration executeStep(
            SimulationSegment simulationSegment,
            DensityInTime densityInTime,
            LocalTime timeThatCarEnterSegment
    ) {
        var segmentStaticValueBasedOnLengthAndNumberOfLanes = durationBasedOnLengthAndNumberOfLanes(simulationSegment);
        var segmentDynamicValues = durationBasedOnDynamicSimulationState(
                simulationSegment,
                densityInTime,
                timeThatCarEnterSegment
        );
        return segmentStaticValueBasedOnLengthAndNumberOfLanes.plus(segmentDynamicValues);
    }

    /**
     * calculate how long vehicle could move between two nodes if there was no traffic etc.
     * Uses static road info: edge length and max speed
     */
    private Duration durationBasedOnLengthAndNumberOfLanes(SimulationSegment simulationSegment) {
        final Edge edge = simulationSegment.edge();
        var edgeLengthMeters = edge.length().asMeters();
        var maxSpeedMetersPerSecond = MaxSpeedTag.get(edge).orElse(Speed.kilometersPerHour(50)).asMetersPerSecond();

        var timeInSeconds = edgeLengthMeters / maxSpeedMetersPerSecond;
        return Duration.ofSeconds((long) timeInSeconds);
    }

    /**
     * calculate how much longer vehicle could move between two nodes based on another vehicles on this segment
     */
    private Duration durationBasedOnDynamicSimulationState(
            SimulationSegment simulationSegment,
            DensityInTime densityInTime,
            LocalTime timeThatCarEnterSegment
    ) {
        var simulationBasedTime = timeCalculations.findTimeInSimulation(timeThatCarEnterSegment);

        return Duration.ofSeconds(5); //todo
    }

}
