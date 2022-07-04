package com.terator.model;

import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.tags.LanesTag;
import org.openstreetmap.atlas.tags.MaxSpeedTag;
import org.openstreetmap.atlas.utilities.scalars.Speed;

public class PointOnMapOnRoad extends LocationWithMetaSpecificParameter {
    public static final double DEFAULT_MAX_SPEED = 50d;
    public static final int DEFAULT_LANES_NUMBER = 1;
    private final Edge edge;

    public PointOnMapOnRoad(Location location, Edge edge) {
        super(location);
        this.edge = edge;
    }

    @Override
    public double getMetaSpecificValue() {
        var maxSpeed = MaxSpeedTag.get(edge).map(Speed::asKilometersPerHour).orElse(DEFAULT_MAX_SPEED);
        var lanesNumber = LanesTag.numberOfLanes(edge).orElse(DEFAULT_LANES_NUMBER);
        return maxSpeed * lanesNumber;
    }
}
