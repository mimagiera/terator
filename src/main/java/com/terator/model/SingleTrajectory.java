package com.terator.model;

import java.time.Instant;

public record SingleTrajectory(Location startLocation, Location endLocation, Instant startTime) {
}
