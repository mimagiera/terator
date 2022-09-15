package com.terator.service.trajectoryListCreator;

import org.openstreetmap.atlas.geography.atlas.items.Area;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.tags.BuildingLevelsTag;

public class SurfaceAreaCalculator {
    /**
     * @return surface area in meter squared
     */
    public static double calculateArea(AtlasEntity atlasEntity) {
        double surfaceOfSingleLevel = getSurfaceAreaOfEntity(atlasEntity);
        double numberOfFloors = numberOfFloors(atlasEntity);

        return surfaceOfSingleLevel * numberOfFloors;
    }

    private static double getSurfaceAreaOfEntity(AtlasEntity atlasEntity) {
        if (atlasEntity instanceof Area area) {
            return area.asPolygon().surface().asMeterSquared();
        } else {
            return 400d;
        }
    }

    private static double numberOfFloors(AtlasEntity atlasEntity) {
        return BuildingLevelsTag.get(atlasEntity).orElse(1d);
    }
}
