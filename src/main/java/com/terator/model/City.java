package com.terator.model;

import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

import java.util.List;

public record City(
        List<AtlasEntity> entities,
        Atlas atlas
) {
}
