package com.terator.service.simulationExecutor;

import com.terator.model.City;
import com.terator.model.SimulationResult;
import com.terator.model.Trajectories;

public interface SimulationExecutor {
    SimulationResult executeSimulation(City city, Trajectories trajectories);
}
