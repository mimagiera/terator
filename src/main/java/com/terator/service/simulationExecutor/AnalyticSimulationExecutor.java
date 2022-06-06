package com.terator.service.simulationExecutor;

import com.terator.model.City;
import com.terator.model.SimulationResult;
import com.terator.model.SingleTrajectory;
import com.terator.model.Trajectories;
import com.terator.model.simulation.DensityInTime;
import com.terator.model.simulation.SimulationSegment;
import com.terator.model.simulation.SimulationState;
import com.terator.service.trajectoryListCreator.LocationExtractor;
import lombok.RequiredArgsConstructor;
import org.openstreetmap.atlas.geography.Latitude;
import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.Longitude;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.geography.atlas.items.Route;
import org.openstreetmap.atlas.geography.atlas.routing.AStarRouter;
import org.openstreetmap.atlas.utilities.scalars.Distance;
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
        var startLocation = LocationExtractor.fromTeratorLocation(singleTrajectory.startLocation());
        var endLocation = LocationExtractor.fromTeratorLocation(singleTrajectory.endLocation());

        final Route route =
                AStarRouter.fastComputationAndSubOptimalRoute(atlas, Distance.MAXIMUM)
                        .route(startLocation, endLocation);

        return Optional.ofNullable(route);
    }

    private SimulationState simulateRoute(Route route, SimulationState simulationState, LocalTime startTime) {
        var nodes = route.nodes();
        var edges = new LinkedList<Edge>();
        route.forEach(edges::add);
        var state = simulationState.state();

        var timeThatCarEnterSegment = startTime;

        for (int i = 0; i < nodes.size() - 1; i++) {
            var startNode = nodes.get(i);
            var endNode = nodes.get(i + 1);
            var simulationSegment = new SimulationSegment(startNode, endNode);
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
     * Uses static road info like length and number of lines
     */
    private Duration durationBasedOnLengthAndNumberOfLanes(SimulationSegment simulationSegment) {
        //todo
        return Duration.ofMinutes(10);
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
