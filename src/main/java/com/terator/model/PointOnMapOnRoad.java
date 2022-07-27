package com.terator.model;

import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.tags.LanesTag;
import org.openstreetmap.atlas.tags.MaxSpeedTag;
import org.openstreetmap.atlas.utilities.scalars.Speed;

import java.util.Map;

public class PointOnMapOnRoad extends LocationWithMetaSpecificParameter {
    public static final double DEFAULT_MAX_SPEED = 50d;
    public static final int DEFAULT_LANES_NUMBER = 1;
    private static final Map<String, Double> WEIGHT_OF_ROAD_TYPE = Map.of(
            "secondary", 1d,
            "residential", 0.3d,
            "service", 0.1d,
            "tertiary", 0.5d,
            "living_street", 0.2d,
            "pedestrian", 0.01d,
            "track", 0.1d,
            "primary", 10d,
            "motorway", 20d
    );
    private final Edge edge;

    public PointOnMapOnRoad(Location location, Edge edge) {
        super(location);
        this.edge = edge;
    }

    @Override
    public double getMetaSpecificValue() {

        var maxSpeed = MaxSpeedTag.get(edge).map(Speed::asKilometersPerHour).orElse(DEFAULT_MAX_SPEED);
        var lanesNumber = LanesTag.numberOfLanes(edge).orElse(DEFAULT_LANES_NUMBER);
        var highwayType = edge.getOsmTags().getOrDefault("highway", "tertiary");
        var weightBasedOnRoadType = WEIGHT_OF_ROAD_TYPE.getOrDefault(highwayType, 0.1d);

        return maxSpeed * lanesNumber * weightBasedOnRoadType;
    }
}
