package com.terator.service.trajectoryListCreator;

import com.terator.model.Location;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.geography.atlas.packed.PackedArea;
import org.openstreetmap.atlas.geography.atlas.packed.PackedRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LocationExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationExtractor.class);

    public static Optional<Location> teratorLocation(AtlasEntity atlasEntity) {
        return locationOfAtlas(atlasEntity).map(LocationExtractor::getLocation);
    }

    private static Optional<org.openstreetmap.atlas.geography.Location> locationOfAtlas(AtlasEntity atlasEntity) {
        if (atlasEntity instanceof PackedArea packedArea) {
            var firstLocation = packedArea.asPolygon().get(0);
            return Optional.of(firstLocation);
        } else if (atlasEntity instanceof PackedRelation packedRelation) {
            var firstLocation = packedRelation.members().get(0).bounds().get(0);
            return Optional.of(firstLocation);
        } else {
            LOGGER.error("Cannot find location of object with type {}", atlasEntity.getClass());
            return Optional.empty();
        }
    }

    private static Location getLocation(org.openstreetmap.atlas.geography.Location atlasLocation) {
        return new Location(atlasLocation.getLongitude().asDegrees(), atlasLocation.getLatitude().asDegrees());
    }

}
