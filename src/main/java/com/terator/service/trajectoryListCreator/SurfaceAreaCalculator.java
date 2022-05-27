package com.terator.service.trajectoryListCreator;

import org.openstreetmap.atlas.geography.atlas.items.Area;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

public class SurfaceAreaCalculator {
    /**
     * @return surface area in meter squared
     */
    public static double calculateArea(AtlasEntity atlasEntity) {
        double surfaceOfSingleLevel = getSurfaceAreaOfEntity(atlasEntity);
        int numberOfFloors = numberOfFloors(atlasEntity);

        return surfaceOfSingleLevel * numberOfFloors;
    }

    private static double getSurfaceAreaOfEntity(AtlasEntity atlasEntity) {
        if (atlasEntity instanceof Area area) {
            return area.asPolygon().surface().asMeterSquared();
        } else {
            return 0;
        }
    }

    private static int numberOfFloors(AtlasEntity atlasEntity) {
        return Integer.parseInt(atlasEntity.getOsmTags().getOrDefault("building:levels", "1"));
    }
}
