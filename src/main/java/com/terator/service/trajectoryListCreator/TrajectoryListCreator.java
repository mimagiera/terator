package com.terator.service.trajectoryListCreator;

import com.terator.model.Trajectories;
import com.terator.model.generatorTable.Probabilities;

public interface TrajectoryListCreator {
    Trajectories createTrajectories(Probabilities probabilities);
}
