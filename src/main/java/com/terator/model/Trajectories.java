package com.terator.model;

import java.io.Serializable;
import java.util.List;

public record Trajectories(
        List<SingleTrajectory> singleTrajectories
) implements Serializable {
}
