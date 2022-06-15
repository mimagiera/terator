package com.terator.model;

import org.openstreetmap.atlas.geography.atlas.items.Edge;

public class PointOnMapOnRoad extends LocationWithMetaSpecificParameter {
    private final Edge edge;

    public PointOnMapOnRoad(org.openstreetmap.atlas.geography.Location location, Edge edge) {
        super(location);
        this.edge = edge;
    }

    @Override
    public double getMetaSpecificValue() {
        return 0; // todo edge specific
    }
}
