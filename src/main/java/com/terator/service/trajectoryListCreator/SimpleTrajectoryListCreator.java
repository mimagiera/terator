package com.terator.service.trajectoryListCreator;

import com.terator.model.Trajectories;
import com.terator.model.generatorTable.Probabilities;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleTrajectoryListCreator implements TrajectoryListCreator {
    @Override
    public Trajectories createTrajectories(Probabilities probabilities) {
        return new Trajectories(List.of());
    }
}
