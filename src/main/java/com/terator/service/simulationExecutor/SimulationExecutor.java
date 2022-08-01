package com.terator.service.simulationExecutor;

import com.terator.model.City;
import com.terator.model.SimulationResult;
import com.terator.model.Trajectories;
import org.apache.commons.lang3.tuple.Pair;
import org.openstreetmap.atlas.geography.atlas.items.Route;

import java.time.LocalTime;
import java.util.List;

public interface SimulationExecutor {
    SimulationResult executeSimulation(List<Pair<Route, LocalTime>> routesWithStartTime);
}
