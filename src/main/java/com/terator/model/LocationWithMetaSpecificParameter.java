package com.terator.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openstreetmap.atlas.geography.Location;

@RequiredArgsConstructor
public abstract class LocationWithMetaSpecificParameter {
    @Getter
    private final Location location;

    public abstract double getMetaSpecificValue();
}
