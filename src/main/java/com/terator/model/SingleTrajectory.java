package com.terator.model;

import java.time.Instant;

public record SingleTrajectory(TeratorLocation startLocation, TeratorLocation endLocation, Instant startTime) {
}
