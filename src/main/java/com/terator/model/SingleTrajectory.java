package com.terator.model;

import org.openstreetmap.atlas.geography.Location;

import java.time.LocalTime;

public record SingleTrajectory(LocalTime startTime, Location startLocation, Location endLocation) {
}
