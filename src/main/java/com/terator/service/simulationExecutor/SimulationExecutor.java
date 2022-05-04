package com.terator.service.simulationExecutor;

import com.terator.model.City;
import com.terator.model.Trajectories;

public interface SimulationExecutor {
    String executeSimulation(City city, Trajectories trajectories);
}
