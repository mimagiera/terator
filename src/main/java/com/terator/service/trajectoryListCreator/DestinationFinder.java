package com.terator.service.trajectoryListCreator;

import com.terator.model.City;
import com.terator.model.Location;
import com.terator.service.generatorCreator.building.BuildingType;
import org.apache.lucene.util.SloppyMath;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DestinationFinder {
    Optional<Location> findDestination(AtlasEntity entity, City city, BuildingType destinationType,
                                       Double perfectDistanceToType
    ) {
        Optional<AtlasEntity> entityWithTheBestSDistance = findProperDestinations(city, destinationType).stream()
                .min(Comparator.comparingDouble(destination ->
                        Math.abs(distanceBetweenEntities(entity, destination) - perfectDistanceToType)
                ));

        return entityWithTheBestSDistance.flatMap(LocationExtractor::teratorLocation);
    }

    private List<AtlasEntity> findProperDestinations(City city, BuildingType destinationType) {
        return destinationType.getEntitiesProvider().apply(city);
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
