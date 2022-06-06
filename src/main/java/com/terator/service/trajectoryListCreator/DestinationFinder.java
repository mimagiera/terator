package com.terator.service.trajectoryListCreator;

import com.terator.model.Location;
import com.terator.service.generatorCreator.building.BuildingType;
import org.apache.lucene.util.SloppyMath;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DestinationFinder {
    Optional<Location> findDestination(AtlasEntity entity, BuildingType destinationType,
                                       Double perfectDistanceToType,
                                       Map<BuildingType, List<AtlasEntity>> allBuildingsByType
    ) {
        Optional<AtlasEntity> entityWithTheBestSDistance = allBuildingsByType.get(destinationType)
                .stream()
                .min(Comparator.comparingDouble(destination ->
                        Math.abs(distanceBetweenEntities(entity, destination) - perfectDistanceToType)
                ));

        return entityWithTheBestSDistance.flatMap(LocationExtractor::teratorLocation);
    }

    private Double distanceBetweenEntities(AtlasEntity start, AtlasEntity end) {
        var l1 = LocationExtractor.teratorLocation(start);
        var l2 = LocationExtractor.teratorLocation(end);

        if (l1.isPresent() && l2.isPresent()) {
            var startLocation = l1.get();
            var endLocation = l2.get();

            return SloppyMath.haversinMeters(
                    startLocation.latitude(), startLocation.longitude(),
                    endLocation.latitude(), endLocation.longitude()
            );
        } else {
            return 0d;
        }
    }
}
