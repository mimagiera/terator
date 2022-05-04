package com.terator.model;

import java.time.Instant;

public record SingleTrajectory(Instant startTime, Location startLocation, Location endLocation) {
}
