package com.terator.model;

import org.openstreetmap.atlas.geography.Location;

public class Building extends LocationWithMetaSpecificParameter {
    private final double area;

    public Building(Location location, double area) {
        super(location);
        this.area = area;
    }

    @Override
    public double getMetaSpecificValue() {
        return area;
    }
}
