package com.terator.model;

import java.time.LocalTime;

public record SingleTrajectory(LocalTime startTime, Location startLocation, Location endLocation) {
}
