package com.terator.service.simulationExecutor;

import com.terator.model.City;
import com.terator.model.SimulationResult;
import com.terator.model.Trajectories;
import org.springframework.stereotype.Service;

@Service
public class AnalyticSimulationExecutor implements SimulationExecutor {

    @Override
    public SimulationResult executeSimulation(City city, Trajectories trajectories
    ) {
        return new SimulationResult();
    }
}
